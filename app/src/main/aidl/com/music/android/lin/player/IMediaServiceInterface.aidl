package com.music.android.lin.player;

import com.music.android.lin.player.metadata.PlayMessage;

interface IMediaServiceInterface {

    void dispatch(inout PlayMessage playMessage);

}