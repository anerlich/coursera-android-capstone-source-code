package au.com.azarel.symptomcheckerservice.client;

import retrofit.RetrofitError;

class UnauthorizedException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String _msg=null;
	UnauthorizedException(RetrofitError cause){
		super(cause);
		_msg=cause.getMessage();
	}
    @Override
    public String getMessage() {
        return _msg;
    }
}

