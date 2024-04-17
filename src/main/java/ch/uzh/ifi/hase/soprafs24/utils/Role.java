package ch.uzh.ifi.hase.soprafs24.utils;

public class Role {
    private String roleName;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public void doNightAction(){}
    public void doDayAction(){}
    public void doVotingAction(){}
}