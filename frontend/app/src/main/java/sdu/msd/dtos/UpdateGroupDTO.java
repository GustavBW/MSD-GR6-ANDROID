package sdu.msd.dtos;

public class UpdateGroupDTO {
    private int idOfActingUser, updateColor;
    private String updateTitle, updateDescription;

    public UpdateGroupDTO(int idOfActingUser, String updateTitle, String updateDescription){
        this.idOfActingUser = idOfActingUser;
        this.updateTitle = updateTitle;
        this.updateDescription = updateDescription;
    }

    public UpdateGroupDTO(int idOfActingUser, String updateTitle, String updateDescription, int updateColor){
        this(idOfActingUser,updateTitle,updateDescription);
        this.updateColor = updateColor;
    }

    public int getIdOfActingUser() {
        return idOfActingUser;
    }

    public int getUpdateColor() {
        return updateColor;
    }

    public String getUpdateDescription() {
        return updateDescription;
    }

    public String getUpdateTitle() {
        return updateTitle;
    }
}
