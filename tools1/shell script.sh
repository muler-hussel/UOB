#!/bin/sh
set -o pipefail

cat non_existent_file.txt | grep "some string" | sort
echo "Exit status: $?"

print_usage() {
    echo "Usage:"
    echo "./b compile <filename or filename.c>  # Compile a C file"
    echo "./b run <filename or filename.c>      # Run the executable"
    echo "./b build <filename or filename.c>    # Compile and run the program"
    exit 1
}

# Compile function
compile() {
    FILENAME="$1"
    
    # Remove .c if provided
    if [ "${FILENAME##*.}" = "c" ]; then
        FILENAME="${FILENAME%.c}"
    fi

    # Compile the C file
    gcc -Wall -std=c11 -g "$FILENAME.c" -o "$FILENAME"

    # Check if gcc command was successful
    if [ $? -eq 0 ]; then
        echo "Compilation successful: $FILENAME"
        return 0
    else
        echo "Compilation failed."
        return 1
    fi
}

# Run function
run() {
    FILENAME="$1"
    
    # Remove .c if provided
    if [ "${FILENAME##*.}" = "c" ]; then
        FILENAME="${FILENAME%.c}"
    fi

    # Check if the executable exists and run it
    if [ -x "./$FILENAME" ]; then
        echo "Running ./$FILENAME"
        ./"$FILENAME"
    else
        echo "Error: Executable ./$FILENAME not found."
        exit 1
    fi
}

# Main script logic
if [ "$#" -eq 0 ]; then
    # No parameters provided, print usage
    print_usage
fi

COMMAND="$1"
FILENAME="$2"

# Ensure a filename is provided with compile/run/build commands
if [ "$COMMAND" = "compile" ] || [ "$COMMAND" = "run" ] || [ "$COMMAND" = "build" ]; then
    if [ -z "$FILENAME" ]; then
        echo "Error: No file name provided."
        print_usage
    fi
fi

case "$COMMAND" in
    compile)
        compile "$FILENAME"
        ;;

    run)
        run "$FILENAME"
        ;;

    build)
        if compile "$FILENAME"; then
            run "$FILENAME"
        fi
        ;;

    *)
        # Unrecognized command, print usage
        echo "Error: Unrecognized command '$COMMAND'."
        print_usage
        ;;
esac
