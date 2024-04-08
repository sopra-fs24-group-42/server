package ch.uzh.ifi.hase.soprafs24.websocket.dto;

public class SelectionRequest {

    private String username;

    private String selection;

    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public String getSelection() {
        return selection;
    }
    
    public void setSelection(String selection) {
        this.selection = selection;
    }
}
