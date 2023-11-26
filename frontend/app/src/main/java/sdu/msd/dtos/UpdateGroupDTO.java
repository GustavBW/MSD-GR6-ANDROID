package sdu.msd.dtos;

public class UpdateGroupDTO {
    private int idOfActingUser, updatedColor, groupId;
    private String updatedTitle, updatedDescription;

    public UpdateGroupDTO(int groupId, int idOfActingUser, String updatedTitle, String updatedDescription){
        this.groupId = groupId;
        this.idOfActingUser = idOfActingUser;
        this.updatedTitle = updatedTitle;
        this.updatedDescription = updatedDescription;
    }

    public UpdateGroupDTO(int groupId, int idOfActingUser, String updateTitle, String updateDescription, int updatedColor){
        this(groupId, idOfActingUser,updateTitle,updateDescription);
        this.updatedColor = updatedColor;
    }

    public int idOfActingUser() {
        return idOfActingUser;
    }

    public int updatedColor() {
        return updatedColor;
    }

    public String updatedDescription() {
        return updatedDescription;
    }

    public String updatedTitle() {
        return updatedTitle;
    }
}
