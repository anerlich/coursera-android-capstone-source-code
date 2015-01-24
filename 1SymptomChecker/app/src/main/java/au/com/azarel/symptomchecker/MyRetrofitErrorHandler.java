package au.com.azarel.symptomchecker;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyRetrofitErrorHandler implements ErrorHandler {
	
	// very basic Retrofit error handler
	
		  @Override public Throwable handleError(RetrofitError cause) {
			  if (cause.isNetworkError()) {
				  return cause;
			  }
		    Response r = cause.getResponse();
		    if (r != null && r.getStatus() != 200) {
		      System.out.println("Login Unsuccessful - response was: " + r.getStatus() + " " + r.getReason());
		    }
		    return cause;
		  }
}


