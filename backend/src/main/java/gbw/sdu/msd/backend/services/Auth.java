package gbw.sdu.msd.backend.services;

public interface Auth {
    boolean mayDeleteUsersFrom(int userInQuestion, int group);
    boolean mayDeleteGroup(int userInQuestion, int group);
}
