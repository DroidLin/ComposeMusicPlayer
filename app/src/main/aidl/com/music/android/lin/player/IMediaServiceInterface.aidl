package com.music.android.lin.player;

import com.music.android.lin.player.metadata.PlayMessage;

interface IMediaServiceInterface {

    void dispatchSync(inout PlayMessage message);

    void dispatchAsync(in PlayMessage message);
}