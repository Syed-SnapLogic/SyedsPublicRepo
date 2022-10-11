package com.snaplogic.snaps.mixed;

import com.google.inject.Inject;
import com.snaplogic.account.api.capabilities.Accounts;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.Document;
import com.snaplogic.snap.api.PropertyValues;
import com.snaplogic.snap.api.SimpleSnap;
import com.snaplogic.snap.api.SnapCategory;
import com.snaplogic.snap.api.capabilities.Category;
import com.snaplogic.snap.api.capabilities.General;
import com.snaplogic.snap.api.capabilities.Version;

import java.util.LinkedHashMap;
import java.util.Map;

@General(title="Snap One", purpose = "some test", docLink = "http://google.com")
@Category(snap = SnapCategory.READ)
@Version(snap = 1)
@Accounts(provides = AccountThree.class)
public class SnapOne extends SimpleSnap {
    private String name;
    @Inject
    private AccountThree account;

    @Override
    public void defineProperties(PropertyBuilder pb) {
        pb.describe("p1", "Name", "your name")
                .required()
                .add();
    }

    @Override
    public void configure(PropertyValues pv) {
        name = pv.get("p1");
    }

    @Override
    public void process (Document d, String i) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("name", name);
        m.put("token", account.connect());
        outputViews.write(documentUtility.newDocument(m));
    }
}
