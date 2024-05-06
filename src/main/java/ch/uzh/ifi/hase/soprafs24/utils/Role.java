package ch.uzh.ifi.hase.soprafs24.utils;

public class Role {
    protected String roleName;
    protected String username;
    protected String selection;

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

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public void doNightAction(){}
}