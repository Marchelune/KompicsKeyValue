package simon.sormain.KeyValueStore.app;


/**
 * This enum should give info to the client about the success or failure of an operation.
 * @author remi
 *
 */
public enum Status {
	SUCCESS
	{
		@Override
		public String details() {
			return "The operation was correctly executed.";
		}
	},
	NOTFOUND
	{
		@Override
		public String details() {
			return "The operation failed because the key was not found.";
		}
	},
	CASFAILED

	{
		@Override
		public String details() {
			return "The CAS operation failed because the reference value did not match the old value";
		}
	};
	public abstract String details();

}
