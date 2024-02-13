#!/bin/bash

# Create a 'bin' directory if it doesn't exist

modulePath="javafx-sdk-21.0.1/lib"
#cd src
# Compile the Java program to the 'bin' directory
javac --module-path "$modulePath" --add-modules javafx.controls,javafx.base,javafx.graphics,javafx.web *.java

# Change to the 'bin' directory

# Run the Java program from the 'bin' directory
java --module-path "$modulePath" --add-modules javafx.controls,javafx.base,javafx.graphics,javafx.web unitStatus

# Remove the compiled class files
rm -f *.class

# Wait for user input before exiting
read -p "Press Enter to exit..."

