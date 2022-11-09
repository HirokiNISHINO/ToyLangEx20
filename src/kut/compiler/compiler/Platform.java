package kut.compiler.compiler;

public enum Platform 
{
	MAC(-1),
	LINUX(-2);
	
	private final int platform;
	
	/**
	 * @param platform
	 */
	private Platform(int platform)
	{
		this.platform = platform;
	}

	/**
	 * @return
	 */
	public int getPlatform() {
		return platform;
	}
}
