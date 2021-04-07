package org.bff.javampd.playlist;

import org.bff.javampd.command.CommandExecutor;
import org.bff.javampd.database.DatabaseProperties;
import org.bff.javampd.database.TagLister;
import org.bff.javampd.song.MPDSong;
import org.bff.javampd.song.SongConverter;
import org.bff.javampd.song.SongDatabase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MPDPlaylistDatabaseTest {

    @Mock
    private SongDatabase songDatabase;
    @Mock
    private CommandExecutor commandExecutor;
    @Mock
    private DatabaseProperties databaseProperties;
    @Mock
    private TagLister tagLister;
    @Mock
    private SongConverter songConverter;

    @InjectMocks
    private MPDPlaylistDatabase playlistDatabase;

    @Test
    void testListSavedPlaylists() {
        String testPlaylistName1 = "testName1";
        String testPlaylistName2 = "testName2";

        List<String> mockedResponseList = new ArrayList<>();
        mockedResponseList.add(testPlaylistName1);
        mockedResponseList.add(testPlaylistName2);

        List<MPDSavedPlaylist> mockList = new ArrayList<>();

        MPDSavedPlaylist mockedSavedPlaylist1 = new MPDSavedPlaylist(testPlaylistName1);
        mockList.add(mockedSavedPlaylist1);
        MPDSavedPlaylist mockedSavedPlaylist2 = new MPDSavedPlaylist(testPlaylistName2);
        mockList.add(mockedSavedPlaylist2);

        when(tagLister.listInfo(TagLister.ListInfoType.PLAYLIST))
                .thenReturn(mockedResponseList);

        List<MPDSavedPlaylist> playlists =
                new ArrayList<>(playlistDatabase.listSavedPlaylists());

        assertEquals(testPlaylistName1, playlists.get(0).getName());
        assertEquals(testPlaylistName2, playlists.get(1).getName());
    }

    @Test
    void testListSavedPlaylistsSongs() {
        String testPlaylistName1 = "testName1";
        String testPlaylistName2 = "testName2";

        String testSongName1 = "testSong1";
        String testSongName2 = "testSong2";

        List<String> mockedResponseList = new ArrayList<>();
        mockedResponseList.add(testPlaylistName1);
        mockedResponseList.add(testPlaylistName2);

        List<MPDSavedPlaylist> mockList = new ArrayList<>();

        MPDSavedPlaylist mockedSavedPlaylist1 = new MPDSavedPlaylist(testPlaylistName1);
        mockList.add(mockedSavedPlaylist1);
        MPDSavedPlaylist mockedSavedPlaylist2 = new MPDSavedPlaylist(testPlaylistName2);
        mockList.add(mockedSavedPlaylist2);

        when(tagLister.listInfo(TagLister.ListInfoType.PLAYLIST))
                .thenReturn(mockedResponseList);

        List<MPDSong> mockedSongs1 = new ArrayList<>();
        mockedSongs1.add(new MPDSong("file1", testSongName1));
        List<MPDSong> mockedSongs2 = new ArrayList<>();
        mockedSongs2.add(new MPDSong("file2", testSongName2));

        when(databaseProperties.getListSongs()).thenReturn("listplaylist");
        when(commandExecutor.sendCommand("listplaylist", testPlaylistName1))
                .thenReturn(mockedResponseList);
        when(commandExecutor.sendCommand("listplaylist", testPlaylistName2))
                .thenReturn(mockedResponseList);

        when(songConverter.getSongFileNameList(mockedResponseList)).thenReturn(mockedResponseList);

        when(songDatabase.searchFileName(testPlaylistName1)).thenReturn(mockedSongs1);
        when(songDatabase.searchFileName(testPlaylistName2)).thenReturn(mockedSongs2);

        List<MPDSavedPlaylist> playlists =
                new ArrayList<>(playlistDatabase.listSavedPlaylists());

        playlists.forEach(playlist -> {
            List<MPDSong> playlistSongs = new ArrayList<>(playlist.getSongs());
            assertEquals(testSongName1, playlistSongs.get(0).getName());
            assertEquals(testSongName2, playlistSongs.get(1).getName());
        });
    }

    @Test
    void testListPlaylists() {
        String testPlaylist = "testPlaylist";

        List<String> mockList = new ArrayList<>();
        mockList.add(testPlaylist);

        when(tagLister.listInfo(TagLister.ListInfoType.PLAYLIST))
                .thenReturn(mockList);

        List<String> playlists =
                new ArrayList<>(playlistDatabase.listPlaylists());

        assertEquals(testPlaylist, playlists.get(0));
    }
}
