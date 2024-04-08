package ch.uzh.ifi.hase.soprafs24.websocket.dto;

public class TestMessage {
    private String message;

    public TestMessage() {  
    }

    public TestMessage(String message) {
        this.message = message;  
    }

    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
