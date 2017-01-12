package edu.uct.research.CrossValidation;

public interface CrossValidationListener {

	public void onEpochComplete(int fold, int epoch);
	public void onFoldComplete(int fold);
	
}
