package com.snaplogic.snaps.mixed;

import com.snaplogic.account.api.Account;
import com.snaplogic.account.api.AccountType;
import com.snaplogic.account.api.capabilities.AccountCategory;
import com.snaplogic.snap.api.capabilities.General;
import com.snaplogic.snap.api.capabilities.Version;

@General(title="Account Two", purpose="some thing", docLink = "http://a.com")
@AccountCategory(type = AccountType.OAUTH2)
@Version(snap = 1)
public class AccountThree implements Account<Object> {
    @Override
    public Object connect() {
        return new Object();
    }

    @Override
    public void disconnect() {
        // NO OP
    }
}
