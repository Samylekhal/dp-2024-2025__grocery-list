// CommandInterface.java
package com.fges.commands;

import java.io.IOException;
import java.util.List;

public interface CommandInterface {
    int execute(List<String> args) throws IOException;
}
