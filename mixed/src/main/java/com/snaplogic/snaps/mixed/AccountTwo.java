package com.snaplogic.snaps.mixed;

import com.snaplogic.account.api.AccountType;
import com.snaplogic.account.api.capabilities.AccountCategory;
import com.snaplogic.snap.api.account.oauth2.DynamicOauth2Account;
import com.snaplogic.snap.api.capabilities.General;
import com.snaplogic.snap.api.capabilities.Version;

@General(title="Account Two", purpose="some thing", docLink = "http://a.com")
@AccountCategory(type = AccountType.OAUTH2)
@Version(snap = 1)
public class AccountTwo extends DynamicOauth2Account<String> implements CustomInterface {
    @Override
    public String connect() {
        return super.getAccessToken();
    }

    @Override
    public void disconnect() {
        // NO OP
    }

    @Override
    public void doCustomization() {
        // NO OP
    }
}
