package com.yngvark.gridwalls.microservices.netcom_forwarder.app.consume_input_file;

class Message {
    private final Command command;
    private final String contents;

    public Message(Command command, String contents) {
        this.command = command;
        this.contents = contents;
    }

    public Command getCommand() {
        return command;
    }

    public String getContents() {
        return contents;
    }

    @Override
    public String toString() {
        return "Message{" +
                "command=" + command +
                ", contents='" + contents + '\'' +
                '}';
    }
}
