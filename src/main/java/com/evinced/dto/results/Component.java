package com.evinced.dto.results;

public class Component {
	private String id;
	private String selector;
	private String index;
	private ComponentProps props;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public ComponentProps getProps() {
		return props;
	}

	public void setProps(ComponentProps props) {
		this.props = props;
	}

	public static class ComponentProps {
		private Object actual;
		private Object expected;

		public Object getActual() {
			return actual;
		}

		public void setActual(Object actual) {
			this.actual = actual;
		}

		public Object getExpected() {
			return expected;
		}

		public void setExpected(Object expected) {
			this.expected = expected;
		}
	}
}
