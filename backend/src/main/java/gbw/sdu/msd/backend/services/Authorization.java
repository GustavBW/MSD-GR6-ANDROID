package gbw.sdu.msd.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Authorization implements Auth{

    private final IUserRegistry userRegistry;
    private final IGroupRegistry groupRegistry;

    @Autowired
    public Authorization(IUserRegistry userRegistry, IGroupRegistry groupRegistry){
        this.userRegistry = userRegistry;
        this.groupRegistry = groupRegistry;
    }

    @Override
    public boolean mayDeleteUsersFrom(int userInQuestion, int actingUser, int group) {
        if(userInQuestion == actingUser){
            return true;
        }
        return mayDeleteUsersFrom(userInQuestion, group);
    }

    @Override
    public boolean mayDeleteUsersFrom(int actingUser, int group) {
        if(groupRegistry.get(group) == null){
            return false;
        }
        return groupRegistry.get(group).admin().id() == actingUser;
    }

    @Override
    public boolean mayDeleteGroup(int actingUser, int group) {
        if(groupRegistry.get(group) == null){
            return false;
        }
        return groupRegistry.get(group).admin().id() == actingUser;
    }
}
