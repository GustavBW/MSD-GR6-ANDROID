package sdu.msd.dtos;

public class UpdateGroupDTO {
    private int idOfActingUser, updatedColor, groupId;
    private String updatedTitle, updatedDescription;

    public UpdateGroupDTO(int idOfActingUser, String updatedTitle, String updatedDescription){
        this.idOfActingUser = idOfActingUser;
        this.updatedTitle = updatedTitle;
        this.updatedDescription = updatedDescription;
    }

    public UpdateGroupDTO(int idOfActingUser, String updatedTitle, String updatedDescription, int updatedColor){
        this(idOfActingUser,updatedTitle,updatedDescription);
        this.updatedColor = updatedColor;
    }

    public int getIdOfActingUser() {
        return idOfActingUser;
    }

    public int getUpdateColor() {
        return updatedColor;
    }

    public String getUpdateDescription() {
        return updatedDescription;
    }

    public String getUpdateTitle() {
        return updatedTitle;
    }
}
