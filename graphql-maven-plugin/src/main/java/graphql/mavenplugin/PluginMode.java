package graphql.mavenplugin;

public enum PluginMode {
	CLIENT("client"), SERVER("server");

	String mode;

	PluginMode(String mode) {
		this.mode = mode;
	}

	public String mode() {
		return mode;
	}
}
