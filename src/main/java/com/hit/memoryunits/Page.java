package com.hit.memoryunits;

import java.util.Arrays;

public class Page<T> implements java.io.Serializable {

	private T _content;
	private java.lang.Long _id;
	
	
	public Page(java.lang.Long id,
            T content) {
		_id = id;
		_content = content;
	}
	
	public java.lang.Long getPageId() {
		return _id;		
	}
	
	public void setPageId(java.lang.Long pageId) {
		_id = pageId;
	}
	
	public T getContent() {
		return _content;
		
	}
	
	public void setContent(T content) {
		_content = content;
	}
	
	public int hashCode() {
		int result = 17;
		result = (result * _id.intValue()) % Integer.MAX_VALUE;
		return result;
	}
	
	public boolean equals(java.lang.Object obj) {
		if(obj == this) {
			return true;
		}
		if(obj instanceof Page<?>) {
			return _id == ((Page<?>)obj).getPageId();
		}
		return false;
	}
	
	public java.lang.String toString() {
		return String.format("Page ID: %s, Content: %s" , _id, _content);
	}
}
