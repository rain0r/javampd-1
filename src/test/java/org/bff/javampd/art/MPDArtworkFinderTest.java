package org.bff.javampd.art;

import org.bff.javampd.MPDException;
import org.bff.javampd.album.MPDAlbum;
import org.bff.javampd.artist.MPDArtist;
import org.bff.javampd.command.CommandExecutor;
import org.bff.javampd.server.ServerProperties;
import org.bff.javampd.song.MPDSong;
import org.bff.javampd.song.SearchProperties;
import org.bff.javampd.song.SongDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MPDArtworkFinderTest {
    private ArtworkFinder artworkFinder;

    @Mock
    private SongDatabase songDatabase;

    @Mock
    private CommandExecutor commandExecutor;

    @Mock
    private SearchProperties searchProperties;

    @Mock
    private ServerProperties serverProperties;


    @BeforeEach
    public void before() {
        artworkFinder = new MPDArtworkFinder(this.songDatabase, this.commandExecutor,
            this.searchProperties, this.serverProperties);
    }

    @Test
    public void findArtist() throws IOException {
        String[] artistImages = new String[]{
                "artist200x200.jpg",
                "artist200x200.png"
        };

        String[] albumImages = new String[]{
                "album200x200.jpg",
                "album200x200.png"
        };

        MPDArtist artist = new MPDArtist("artist");

        String prefix = decode(new File(this.getClass().getResource("/images/artist/" + artistImages[0]).getFile()).getParent())
                + File.separator;

        String albumSuffix = "album" + File.separator;

        List<MPDSong> songs = new ArrayList<>();
        songs.add(new MPDSong(prefix + albumSuffix + "song1", "song1"));
        songs.add(new MPDSong(prefix + albumSuffix + "song2", "song2"));

        when(songDatabase.findArtist(artist)).thenReturn(songs);

        List<MPDArtwork> artworkList = artworkFinder.find(artist);

        assertEquals(artistImages.length + albumImages.length, artworkList.size());

        Map<String, byte[]> btyeMap = new HashMap<>();
        btyeMap.put(artistImages[0], Files.readAllBytes(new File(prefix + artistImages[0]).toPath()));
        btyeMap.put(artistImages[1], Files.readAllBytes(new File(prefix + artistImages[1]).toPath()));
        btyeMap.put(albumImages[0], Files.readAllBytes(new File(prefix + albumSuffix + albumImages[0]).toPath()));
        btyeMap.put(albumImages[1], Files.readAllBytes(new File(prefix + albumSuffix + albumImages[1]).toPath()));


        Arrays.asList(artistImages).forEach(image -> {
            MPDArtwork foundArtwork = artworkList
                    .stream()
                    .filter(artwork -> image.equals(artwork.getName()))
                    .findFirst()
                    .orElse(null);
            assertEquals(prefix + image, foundArtwork.getPath());
            assertTrue(Arrays.equals(btyeMap.get(foundArtwork.getName()), foundArtwork.getBytes()));
        });

        Arrays.asList(albumImages).forEach(image -> {
            MPDArtwork foundArtwork = artworkList
                    .stream()
                    .filter(artwork -> image.equals(artwork.getName()))
                    .findFirst()
                    .orElse(null);
            assertEquals(prefix + albumSuffix + image, foundArtwork.getPath());
            assertTrue(Arrays.equals(btyeMap.get(foundArtwork.getName()), foundArtwork.getBytes()));
        });
    }

    @Test
    public void findArtistPrefix() throws UnsupportedEncodingException {
        String[] artistImages = new String[]{
                "artist200x200.jpg",
                "artist200x200.png"
        };

        String[] albumImages = new String[]{
                "album200x200.jpg",
                "album200x200.png"
        };

        MPDArtist artist = new MPDArtist("artist");

        String path = decode(new File(this.getClass().getResource("/images/artist/" + artistImages[0]).getFile()).getParent());
        List<MPDSong> songs = new ArrayList<>();
        songs.add(new MPDSong("/album/song1", "song1"));
        songs.add(new MPDSong("/album/song2", "song2"));

        when(songDatabase.findArtist(artist)).thenReturn(songs);

        List<MPDArtwork> artworkList = artworkFinder.find(artist, path);

        assertEquals(artistImages.length + albumImages.length, artworkList.size());

        Arrays.asList(artistImages).forEach(image -> {
            MPDArtwork foundArtwork = artworkList
                    .stream()
                    .filter(artwork -> image.equals(artwork.getName()))
                    .findFirst()
                    .orElse(null);
            assertNotNull(foundArtwork.getPath());
            assertNotNull(foundArtwork.getBytes());
        });

        Arrays.asList(albumImages).forEach(image -> {
            MPDArtwork foundArtwork = artworkList
                    .stream()
                    .filter(artwork -> image.equals(artwork.getName()))
                    .findFirst()
                    .orElse(null);
            assertNotNull(foundArtwork.getPath());
            assertNotNull(foundArtwork.getBytes());
        });
    }

    @Test
    public void findArtistBadPath() throws UnsupportedEncodingException {
        String[] artistImages = new String[]{
                "artist200x200.png"
        };

        MPDArtist artist = new MPDArtist("artist");

        String path1 = decode(new File(this.getClass().getResource("/images/" + artistImages[0]).getFile()).getParent());
        List<MPDSong> songs = new ArrayList<>();
        songs.add(new MPDSong(path1, "song1"));

        when(songDatabase.findArtist(artist)).thenReturn(songs);

        List<MPDArtwork> artworkList = artworkFinder.find(artist);

        assertEquals(0, artworkList.size());
    }

    @Test
    public void findAlbum() throws UnsupportedEncodingException {
        String[] albumImages = new String[]{
                "album200x200.jpg",
                "album200x200.png"
        };

        MPDAlbum album = new MPDAlbum("album", "artist");

        String path1 = decode(new File(this.getClass().getResource("/images/artist/album/" + albumImages[0]).getFile()).getParent());
        List<MPDSong> songs = new ArrayList<>();
        songs.add(new MPDSong(path1 + "/song1", "song1"));

        when(songDatabase.findAlbum(album)).thenReturn(songs);

        List<MPDArtwork> artworkList = artworkFinder.find(album);

        assertEquals(albumImages.length, artworkList.size());

        Arrays.asList(albumImages).forEach(image -> {
            MPDArtwork foundArtwork = artworkList
                    .stream()
                    .filter(artwork -> image.equals(artwork.getName()))
                    .findFirst()
                    .orElse(null);
            assertNotNull(foundArtwork.getPath());
            assertNotNull(foundArtwork.getBytes());
        });
    }

    @Test
    public void findAlbumPrefix() throws UnsupportedEncodingException {
        String[] albumImages = new String[]{
                "album200x200.jpg",
                "album200x200.png"
        };

        MPDAlbum album = new MPDAlbum("album", "artist");

        String path = decode(new File(this.getClass().getResource("/images/artist/album/" + albumImages[0]).getFile()).getParent());
        List<MPDSong> songs = new ArrayList<>();
        songs.add(new MPDSong("/song1", "song1"));

        when(songDatabase.findAlbum(album)).thenReturn(songs);

        List<MPDArtwork> artworkList = artworkFinder.find(album, path);

        assertEquals(albumImages.length, artworkList.size());

        Arrays.asList(albumImages).forEach(image -> {
            MPDArtwork foundArtwork = artworkList
                    .stream()
                    .filter(artwork -> image.equals(artwork.getName()))
                    .findFirst()
                    .orElse(null);
            assertNotNull(foundArtwork.getPath());
            assertNotNull(foundArtwork.getBytes());
        });
    }

    @Test
    public void findPath() throws UnsupportedEncodingException {
        String[] images = new String[]{
                "artist200x200.jpg",
                "artist200x200.png"
        };

        String testImage = decode(new File(this.getClass().getResource("/images/artist/" + images[0]).getFile()).getParent());
        List<MPDArtwork> artworkList = artworkFinder.find(testImage);

        assertEquals(images.length, artworkList.size());

        Arrays.asList(images).forEach(image -> {
            MPDArtwork foundArtwork = artworkList
                    .stream()
                    .filter(artwork -> image.equals(artwork.getName()))
                    .findFirst()
                    .orElse(null);
            assertNotNull(foundArtwork.getPath());
            assertNotNull(foundArtwork.getBytes());
        });
    }

    @Test
    public void findPathIOException() throws IOException {
        File testFile = File.createTempFile("test", ".jpg");

        testFile.setReadable(false);

        assertThrows(MPDException.class, () -> artworkFinder.find(testFile.getParent()));
    }

//    @Test
//    public void findPathDirectoryIOException() {
//        File tempDir = new File(System.getProperty("java.io.tmpdir") + "imageTemp");
//        File testFile = null;
//        try {
//            testFile = File.createTempFile("test", ".jpg", tempDir);
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//            e.printStackTrace();
//        }
//        File finalTestFile = testFile;
//        assertThrows(MPDException.class, () -> artworkFinder.find(finalTestFile.getParent()));
//    }

    @Test
    public void findBadPath() {
        assertThrows(MPDException.class, () -> artworkFinder.find("bad"));
    }

    private String decode(String encodedString) throws UnsupportedEncodingException {
        return URLDecoder.decode(encodedString, StandardCharsets.UTF_8);
    }
}