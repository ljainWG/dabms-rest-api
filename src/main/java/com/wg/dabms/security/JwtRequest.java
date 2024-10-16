package com.wg.dabms.security;

import lombok.Data;

@Data
public class JwtRequest {
	String userName;
	String password;
}
