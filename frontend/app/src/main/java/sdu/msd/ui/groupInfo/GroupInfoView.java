package sdu.msd.ui.groupInfo;

import static sdu.msd.ui.home.HomeView.getApi;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sdu.msd.apiCalls.GroupAPIService;

public class GroupInfoView extends AppCompatActivity {
    int userId, groupId;
    private GroupAPIService apiService;
    private static final String BASEURL =  getApi() + "groups/";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getIntent().getIntExtra("userId",-1);
        groupId = getIntent().getIntExtra("groupId",-1);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(GroupAPIService.class);
    }
}

