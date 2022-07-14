package issue140;

public class Issue140Discussion {

	/**
	 * <PRE>
	interface IBar {
	  name: String
	}
	 * 
	 * </PRE>
	 */
	public interface IBar {
		public void setName(String name);
	}

	/**
	 * <PRE>
	interface I2Bar implements IBar {
	  name: String
	}
	 * </PRE>
	 */
	public interface I2Bar extends IBar {
		@Override
		public void setName(String name);
	}

	/**
	 * <PRE>
	type Bar implements I2Bar & IBar {
	  name: String
	}
	 * </PRE>
	 */
	public class Bar implements I2Bar {
		@Override
		public void setName(String name) {
			// TODO Auto-generated method stub
		}
	}

	/**
	 * <PRE>
	interface IFoo {
	  bar: IBar
	}
	 * </PRE>
	 */
	public interface IFoo {
		public void setBar(IBar bar);
	}

	/**
	 * <PRE>
	interface I2Foo implements IFoo {
	  bar: I2Bar
	}
	 * </PRE>
	 */
	public interface I2Foo extends IFoo {
		@Override
		default public void setBar(IBar bar) {
			if (bar == null || bar instanceof I2Bar) {
				setBar((I2Bar) bar);
			}
			throw new IllegalArgumentException(
					"The 'bar' parameter should be an instance of 'I2Bar', but is an instance of '"
							+ bar.getClass().getName() + "'");
		}

		public void setBar(I2Bar bar);
	}

	/**
	 * <PRE>
	type Foo implements I2Foo & IFoo {
	  bar: Bar
	}
	 * </PRE>
	 */
	public class Foo implements I2Foo, IFoo {
		@Override
		public void setBar(I2Bar bar) {
			// TODO Auto-generated method stub
		}
	}
}
