package com.richo.test.dropwizard.persistence;

import com.richo.test.dropwizard.model.Token;

public interface TokenBlacklist
{
	void blacklist(Token token);

	boolean isBlackListed(Token token);
}
