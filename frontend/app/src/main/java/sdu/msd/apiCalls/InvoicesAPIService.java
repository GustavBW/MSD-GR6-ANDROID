package sdu.msd.apiCalls;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import sdu.msd.dtos.InvoiceDTO;

public interface InvoicesAPIService {
    @GET("{userId}")
    Call<List<InvoiceDTO>> getInvoicesForUser(@Path("userId") int userId);

    @GET("by-id/{invoiceId}")
    Call<InvoiceDTO> getInvoice(@Path("invoiceId") int invoiceId);
}
