#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <pthread.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <gtk/gtk.h>
#include <time.h>

#define PORT 8080
#define MAX_CLIENTS 5

typedef struct {
    char message[256];
} ThreadArgs;

// Global variables to share data between threads
GtkTextBuffer *logBuffer;
pthread_mutex_t logMutex = PTHREAD_MUTEX_INITIALIZER;

gboolean displayMessageCallback(gpointer data);

void *displayMessage(void *args) {
    ThreadArgs *threadArgs = (ThreadArgs *)args;

    // Tokenize the message
    char *token = strtok(threadArgs->message, ",");
    char room[32], floor[32], building[32];

    if (token != NULL) {
        strcpy(room, token);

        token = strtok(NULL, ",");
        if (token != NULL) {
            strcpy(floor, token);

            token = strtok(NULL, ",");
            if (token != NULL) {
                strcpy(building, token);
            }
        }
    }

    // Get the current timestamp
    time_t rawtime;
    struct tm *info;
    char timestamp[20];  // Adjust the size based on your formatting needs

    time(&rawtime);
    info = localtime(&rawtime);
    strftime(timestamp, sizeof(timestamp), "%Y-%m-%d %H:%M:%S", info);

    // Create a formatted log message
    char logMessage[256];
    snprintf(logMessage, sizeof(logMessage), "[%s] Room: %s, Floor: %s, Building: %s", timestamp, room, floor, building);

    // Create a copy of the formatted log message for GTK, since it needs to be freed in the main thread
    char *logMessageCopy = strdup(logMessage);

    // Use gdk_threads_add_idle to add the display message task to the GTK main loop
    gdk_threads_add_idle(displayMessageCallback, logMessageCopy);

    free(threadArgs);

    pthread_exit(NULL);
}

gboolean displayMessageCallback(gpointer data) {
    char *logMessageCopy = (char *)data;

    // GTK code
    GtkWidget *window;
    GtkWidget *box;
    GtkWidget *label;
    GtkWidget *button;

    // Create a vertical box
    box = gtk_box_new(GTK_ORIENTATION_VERTICAL, 5);

    // Add label to the box
    label = gtk_label_new(logMessageCopy);
    gtk_box_pack_start(GTK_BOX(box), label, TRUE, TRUE, 0);

    // Add button to the box
    button = gtk_button_new_with_label("Close");
    g_signal_connect(button, "clicked", G_CALLBACK(gtk_widget_destroy), NULL);
    gtk_box_pack_start(GTK_BOX(box), button, TRUE, TRUE, 0);

    // Create a new window
    window = gtk_window_new(GTK_WINDOW_TOPLEVEL);
    gtk_window_set_title(GTK_WINDOW(window), "ALERT");
    gtk_window_set_default_size(GTK_WINDOW(window), 300, 150);

    // Set the window to be always on top
    gtk_window_set_keep_above(GTK_WINDOW(window), TRUE);

    // Add the box to the window
    gtk_container_add(GTK_CONTAINER(window), box);

    // Show all widgets
    gtk_widget_show_all(window);

    // Acquire the mutex before accessing the log buffer
    pthread_mutex_lock(&logMutex);

    // Append the log message to the log buffer
    GtkTextIter iter;
    gtk_text_buffer_get_end_iter(logBuffer, &iter);
    gtk_text_buffer_insert(logBuffer, &iter, logMessageCopy, -1);
    gtk_text_buffer_insert(logBuffer, &iter, "\n", -1);

    // Release the mutex after modifying the log buffer
    pthread_mutex_unlock(&logMutex);

    // Free the duplicated text
    g_free(logMessageCopy);

    return G_SOURCE_REMOVE;  // Remove the idle callback after execution
}

void *handleClient(void *clientSocket) {
    int clientSocketFd = *((int *)clientSocket);
    char buffer[256];

    recv(clientSocketFd, buffer, sizeof(buffer), 0);

    ThreadArgs *threadArgs = (ThreadArgs *)malloc(sizeof(ThreadArgs));
    strcpy(threadArgs->message, buffer);

    pthread_t thread;
    pthread_create(&thread, NULL, displayMessage, (void *)threadArgs);

    close(clientSocketFd);
    pthread_exit(NULL);
}

void *mainUiThread(void *data) {
    // GTK main loop for the main UI
    gtk_main();

    pthread_exit(NULL);
}

int main() {
    gdk_threads_init();
    int serverSocket, clientSocket, addrLen;
    struct sockaddr_in serverAddr, clientAddr;

    pthread_t thread[MAX_CLIENTS];
    int threadCount = 0;

    if ((serverSocket = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
        perror("Socket creation failed");
        exit(EXIT_FAILURE);
    }

    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(PORT);
    serverAddr.sin_addr.s_addr = INADDR_ANY;

    if (bind(serverSocket, (struct sockaddr *)&serverAddr, sizeof(serverAddr)) == -1) {
        perror("Bind failed");
        exit(EXIT_FAILURE);
    }

    if (listen(serverSocket, MAX_CLIENTS) == -1) {
        perror("Listen failed");
        exit(EXIT_FAILURE);
    }

    printf("Server listening on port %d...\n", PORT);

    // Initialize GTK
    gtk_init(NULL, NULL);

    // Create a main UI window (full screen)
    GtkWidget *mainWindow = gtk_window_new(GTK_WINDOW_TOPLEVEL);
    gtk_window_set_title(GTK_WINDOW(mainWindow), "Server Log");
    gtk_window_set_default_size(GTK_WINDOW(mainWindow), gdk_screen_width(), gdk_screen_height());

    // Create a text view and buffer for the log
    GtkWidget *textView = gtk_text_view_new();
    GtkWidget *scrolledWindow = gtk_scrolled_window_new(NULL, NULL);
    gtk_scrolled_window_set_policy(GTK_SCROLLED_WINDOW(scrolledWindow), GTK_POLICY_AUTOMATIC, GTK_POLICY_AUTOMATIC);
    gtk_container_add(GTK_CONTAINER(scrolledWindow), textView);
    gtk_container_add(GTK_CONTAINER(mainWindow), scrolledWindow);

    logBuffer = gtk_text_view_get_buffer(GTK_TEXT_VIEW(textView));
    gtk_text_view_set_editable(GTK_TEXT_VIEW(textView), FALSE);
    gtk_text_view_set_wrap_mode(GTK_TEXT_VIEW(textView), GTK_WRAP_WORD_CHAR);

    // Show all widgets
    gtk_widget_show_all(mainWindow);

    // Create a thread for the main UI
    pthread_t uiThread;
    pthread_create(&uiThread, NULL, mainUiThread, NULL);

    while (1) {
        addrLen = sizeof(clientAddr);
        if ((clientSocket = accept(serverSocket, (struct sockaddr *)&clientAddr, (socklen_t *)&addrLen)) == -1) {
            perror("Accept failed");
            exit(EXIT_FAILURE);
        }

        pthread_create(&thread[threadCount], NULL, handleClient, (void *)&clientSocket);
        threadCount++;
    }

    close(serverSocket);

    // Wait for the main UI thread to finish
    pthread_join(uiThread, NULL);

    return 0;
}
