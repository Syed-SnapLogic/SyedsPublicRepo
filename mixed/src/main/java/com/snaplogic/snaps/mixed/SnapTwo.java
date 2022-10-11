package com.snaplogic.snaps.mixed;

import com.google.inject.*;
import com.google.inject.Module;
import com.snaplogic.account.api.Account;
import com.snaplogic.account.api.capabilities.Accounts;
import com.snaplogic.account.api.capabilities.MultiAccountBinding;
import com.snaplogic.snap.api.Document;
import com.snaplogic.snap.api.SimpleSnap;
import com.snaplogic.snap.api.SnapCategory;
import com.snaplogic.snap.api.capabilities.Category;
import com.snaplogic.snap.api.capabilities.General;
import com.snaplogic.snap.api.capabilities.Version;

import java.util.LinkedHashMap;
import java.util.Map;

@General(title="Snap Two", purpose = "something", docLink = "http://b.com")
@Category(snap = SnapCategory.READ)
@Accounts(provides = {AccountTwo.class, AccountOne.class})
@Version(snap=1)
public class SnapTwo extends SimpleSnap implements MultiAccountBinding<CustomInterface> {
    @Inject
    private Account<String> account;

    @Override
    public void process(Document d, String t) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("dynamicToken", account.connect());
        outputViews.write(documentUtility.newDocument(m));
    }

    @Override
    public Module getManagedAccountModule(CustomInterface stringAccount) {
        return new AbstractModule() {
            @Override
            protected void configure() {
                bind(Key.get(new TypeLiteral<CustomInterface>() {
                }))
                        .toInstance(stringAccount);
            }
        };
    }
}
