package ch.uzh.ifi.hase.soprafs24.utils;

public class Role {
    private Sting roleName;
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

    public Sting getRoleName() {
        return roleName;
    }

    public void setRoleName(Sting roleName) {
        this.roleName = roleName;
    }

    public void doNightAction(){}
}