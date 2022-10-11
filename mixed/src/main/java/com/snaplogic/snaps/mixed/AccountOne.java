package com.snaplogic.snaps.mixed;

import com.snaplogic.account.api.Account;
import com.snaplogic.account.api.AccountType;
import com.snaplogic.account.api.capabilities.AccountCategory;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.PropertyValues;
import com.snaplogic.snap.api.capabilities.General;
import com.snaplogic.snap.api.capabilities.Version;

@General(title="Account one", purpose = "sp1", docLink = "http://snaplogic.com")
@AccountCategory(type = AccountType.CUSTOM)
@Version(snap = 1)
public class AccountOne implements Account<String>, CustomInterface {
    private String token;

    @Override
    public void defineProperties(final PropertyBuilder pb) {
        pb.describe("token", "Token", "some thing")
                .required()
                .add();
    }

    @Override
    public void configure(final PropertyValues pv) {
        token = pv.get("token");
    }

    @Override
    public String connect() {
        return token;
    }

    @Override
    public void disconnect() {
        // NO OP
    }

    @Override
    public void doCustomization() {

    }
}
