// IMediaServiceInterface.aidl
package com.music.android.lin.player;

import com.music.android.lin.player.metadata.PlayMessage;

// Declare any non-default types here with import statements

interface IMediaServiceInterface {

    void dispatchMessage(inout PlayMessage message);
}