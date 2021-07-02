package com.davcamalv.filmApp.dtos;

public class MessageDTO {

	private String message;

	private String sender;

	private boolean specialKeyboard;

	private SelectableDTO selectable;

	public MessageDTO(String message, String sender, boolean specialKeyboard, SelectableDTO selectable) {
		super();
		this.message = message;
		this.sender = sender;
		this.specialKeyboard = specialKeyboard;
		this.selectable = selectable;
	}

	public MessageDTO() {
		super();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public boolean getSpecialKeyboard() {
		return specialKeyboard;
	}

	public void setSpecialKeyboard(boolean specialKeyboard) {
		this.specialKeyboard = specialKeyboard;
	}

	public SelectableDTO getSelectable() {
		return selectable;
	}

	public void setSelectable(SelectableDTO selectable) {
		this.selectable = selectable;
	}
	
	

}
