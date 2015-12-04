package com.picopwr.plop.utility;

import com.picopwr.plop.model.User;

/**
 * Created by duy on 11/4/15.
 */
interface GetUserCallback {
    // Any activity that uses this class must implement done:
    public abstract void done(User returnedUser);
}
