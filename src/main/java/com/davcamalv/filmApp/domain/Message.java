package com.davcamalv.filmApp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.davcamalv.filmApp.enums.SenderType;

@Entity
@Table(name = "message")
public class Message {
	@Id
	@NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@NotNull
	@Column(name = "message", length=65735)
	private String message;
	
	@NotNull
	@Column(name = "sender")
	@Enumerated(EnumType.STRING)
	private SenderType sender;
	
	@OnDelete(action = OnDeleteAction.CASCADE)
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
    private User user;
	
	@NotNull
	@Column(name = "special_keyboard")
	private boolean specialKeyboard;
	
	@OneToOne
	@JoinColumn(name = "selectable_id", nullable = true)
    private Selectable selectable;
	
	@NotNull
	@Column(name = "full_width")
	private Boolean fullWidth;
	
	public Message() {
		super();
	}

	public Message(String message, SenderType sender, User user, boolean specialKeyboard, Selectable selectable, Boolean fullWidth) {
		super();
		this.message = message;
		this.sender = sender;
		this.user = user;
		this.specialKeyboard = specialKeyboard;
		this.selectable = selectable;
		this.fullWidth = fullWidth;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public SenderType getSender() {
		return sender;
	}

	public void setSender(SenderType sender) {
		this.sender = sender;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean getSpecialKeyboard() {
		return specialKeyboard;
	}

	public void setSpecialKeyboard(boolean specialKeyboard) {
		this.specialKeyboard = specialKeyboard;
	}

	public Selectable getSelectable() {
		return selectable;
	}

	public void setSelectable(Selectable selectable) {
		this.selectable = selectable;
	}

	public Boolean getFullWidth() {
		return fullWidth;
	}

	public void setFullWidth(Boolean fullWidth) {
		this.fullWidth = fullWidth;
	}

}
