
public interface iCanvisBloc {
	
    public enum BlocTocat {
        ESQUERRA,
        DRETA,
        DALT,
        BAIX
    }
	
	public void blocDestruit(Bloc b, BlocTocat t, boolean destruit);
}
