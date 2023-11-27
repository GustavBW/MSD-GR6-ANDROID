package sdu.msd.ui.Invoices;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import static sdu.msd.ui.home.HomeView.getApi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sdu.msd.R;
import sdu.msd.apiCalls.InvoicesAPIService;
import sdu.msd.dtos.GroupDTO;
import sdu.msd.dtos.InvoiceDTO;
import sdu.msd.ui.Group.GroupView;
import sdu.msd.ui.home.HomeView;

public class InvoicesView extends AppCompatActivity {
    private Button back;
    private LinearLayout layout;
    private Retrofit retrofit;
    private int userId;
    private SharedPreferences sharedPreferences;
    private DecimalFormat decimalFormat;
    private InvoicesAPIService invoicesAPIService;

    private static final String BASEINVOICEURL = getApi() + "invoices/";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_invoices);
        back = findViewById(R.id.back);
        layout = findViewById(R.id.invoicesContainer);
        retrofit = new Retrofit.Builder()
                .baseUrl(BASEINVOICEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);
        invoicesAPIService = retrofit.create(InvoicesAPIService.class);
        getInvoices();
        back();


    }

    private void back() {
        back.setOnClickListener(view -> {
            Intent intent = new Intent(InvoicesView.this, HomeView.class);
            startActivity(intent);
        });
    }

    private void getInvoices() {
        Call<List<InvoiceDTO>> call = invoicesAPIService.getInvoicesForUser(userId);
        call.enqueue(new Callback<List<InvoiceDTO>>() {
            @Override
            public void onResponse(Call<List<InvoiceDTO>> call, Response<List<InvoiceDTO>> response) {
                if(response.isSuccessful() && response.body()!= null){
                    updateInvoicesView(response.body());
                }

            }

            @Override
            public void onFailure(Call<List<InvoiceDTO>> call, Throwable t) {
                Toast.makeText(InvoicesView.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    private void updateInvoicesView(List<InvoiceDTO> invoiceDTOS) {
        for (int i = invoiceDTOS.size() - 1; i >= 0; i--) {
            InvoiceDTO invoiceDTO = invoiceDTOS.get(i);
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setCornerRadius(getResources().getDimension(R.dimen.corner_radius));
            gradientDrawable.setColor(Color.parseColor("#f3f3f3"));
            TextView textView = new Button(this);
            @SuppressLint("SimpleDateFormat") String currentDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            textView.setText("Date: " + currentDate + "\n" +
                    "From " + invoiceDTO.paymentFrom().username() + "\n" +
                    "To " + invoiceDTO.paymentTo().username() + "\n" +
                    "Amount = " + invoiceDTO.amount() + " DKK");

            LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{gradientDrawable});
            textView.setTextColor(Color.BLACK);
            textView.setBackground(layerDrawable);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.activity_vertical_margin));
            textView.setLayoutParams(layoutParams);

            layout.addView(textView);

            textView.setTextSize(20);
            textView.setOnClickListener(view -> {
                Intent intent = new Intent(InvoicesView.this, HomeView.class);
                startActivity(intent);
            });
            View separator = new View(this);
            separator.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (int) getResources().getDimension(R.dimen.corner_radius)));
            separator.setBackground(getResources().getDrawable(R.drawable.divider_line));
            layout.addView(separator);
        }
    }

}
