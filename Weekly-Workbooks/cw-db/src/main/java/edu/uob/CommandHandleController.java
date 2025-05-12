package edu.uob;

import java.io.IOException;

public interface CommandHandleController {
    String handle(String command) throws IOException;
}
