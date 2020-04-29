/*
 * Copyright (c) 2020 MemoryOfLife
 * This file (RequestHandler.java) and its related project (BanList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package it.marko.banlist.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import it.marko.banlist.Main;

import java.io.IOException;

public abstract class RequestHandler implements HttpHandler {
    @Override
    public abstract void handle(HttpExchange httpExchange) throws IOException;

    protected void log(String reason, Throwable throwable) {
        Main.getInstance().printError(reason, throwable);
    }

    protected void log(Throwable throwable) {
        Main.getInstance().printError(throwable);
    }

    protected void log(String reason) {
        Main.getInstance().printInfo(reason);
    }
}
