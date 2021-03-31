package org.bff.javampd.art;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bff.javampd.MPDException;
import org.bff.javampd.album.MPDAlbum;
import org.bff.javampd.artist.MPDArtist;
import org.bff.javampd.command.CommandExecutor;
import org.bff.javampd.server.ServerProperties;
import org.bff.javampd.song.SearchProperties;
import org.bff.javampd.song.SongDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class MPDArtworkFinder implements ArtworkFinder {

  private static final Logger LOGGER = LoggerFactory.getLogger(MPDArtworkFinder.class);

  private SongDatabase songDatabase;

  private CommandExecutor commandExecutor;

  private SearchProperties searchProperties;

  private ServerProperties serverProperties;

  @Inject
  public MPDArtworkFinder(SongDatabase songDatabase, CommandExecutor commandExecutor,
      SearchProperties searchProperties, ServerProperties serverProperties) {
    this.songDatabase = songDatabase;
    this.commandExecutor = commandExecutor;
    this.searchProperties = searchProperties;
    this.serverProperties = serverProperties;
  }

  @Override
  public MPDArtwork findArtwork(String path) {
    MPDArtwork artwork = new MPDArtwork("", "");
    LOGGER.debug("path: {}", path);
    List<String> commandResponse =
        commandExecutor.sendCommand(searchProperties.getAlbumArt(), path, "0");

    byte[] data = new byte[0];
    try {
      data = convertResponseToArtwork(commandResponse);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    artwork.setBytes(data);

    return artwork;
  }

  @Override
  public List<MPDArtwork> find(MPDAlbum album) {
    return find(album, "");
  }

  @Override
  public List<MPDArtwork> find(MPDAlbum album, String pathPrefix) {
    List<MPDArtwork> artworkList = new ArrayList<>();
    List<String> paths = new ArrayList<>();

    this.songDatabase.findAlbum(album)
        .forEach(song -> paths.add(
            pathPrefix + song.getFile().substring(0, song.getFile().lastIndexOf(File.separator))));

    paths.stream().distinct().forEach(path -> artworkList.addAll(find(path)));

    return artworkList;
  }

  @Override
  public List<MPDArtwork> find(MPDArtist artist) {
    return find(artist, "");
  }

  @Override
  public List<MPDArtwork> find(MPDArtist artist, String pathPrefix) {
    List<MPDArtwork> artworkList = new ArrayList<>();
    List<String> paths = new ArrayList<>();
    List<String> albumPaths = new ArrayList<>();

    this.songDatabase.findArtist(artist)
        .forEach(song -> albumPaths.add(pathPrefix + song.getFile().substring(0, song.getFile()
            .lastIndexOf(File.separator))));

    albumPaths.forEach(path -> {
      if (path.contains(File.separator + artist.getName() + File.separator)) {
        paths.add(path.substring(0, path.lastIndexOf(File.separator)));
      }
    });

    paths.addAll(albumPaths);
    paths.stream().distinct().forEach(path -> artworkList.addAll(find(path)));

    return artworkList;
  }

  @Override
  public List<MPDArtwork> find(String path) {
    List<MPDArtwork> artworkList = new ArrayList<>();

    try (DirectoryStream<Path> stream = Files
        .newDirectoryStream(Paths.get(path), "**.{jpg,jpeg,png}")) {
      stream.forEach(file -> artworkList.add(loadArtwork(file)));
    } catch (IOException e) {
      LOGGER.error("Could not load art in {}", path, e);
      throw new MPDException("Could not read path: " + path, e);
    }

    return artworkList;
  }

  private static MPDArtwork loadArtwork(Path file) {
    file.getFileName();
    MPDArtwork artwork = new MPDArtwork(file.getFileName().toString(),
        file.toAbsolutePath().toString());
    artwork.setBytes(loadFile(file));

    return artwork;
  }

  private static byte[] loadFile(Path path) {
    try {
      return Files.readAllBytes(path);
    } catch (IOException e) {
      LOGGER.error("Could not load art in {}", path, e);
      throw new MPDException("Could not read path: " + path, e);
    }
  }

  private byte[] convertResponseToArtwork(List<String> response)
      throws UnsupportedEncodingException {
    Iterator<String> iterator = response.iterator();
    // Skip the size
    String sizeLine = iterator.next();
    int size = Integer.parseInt(sizeLine.substring(sizeLine.split(":")[0].length() + 1).trim());
    // Skip bytes read
    String bytesLine = iterator.next();
    int bytes = Integer.parseInt(bytesLine.substring(bytesLine.split(":")[0].length() + 1).trim());
    ArrayList<Byte> bufferList = new ArrayList<>();
    int sum = 0;
    int iterCount = 0;
    String line = null;

    LOGGER.debug("Size: {}", size);
    LOGGER.debug("bytes: {}", bytes);

    String hex = "";

    while (iterator.hasNext()) {
      iterCount += 1;
      line = iterator.next();
//      LOGGER.debug(line);
      for (byte x : line.getBytes(serverProperties.getEncoding())) {
        hex += String.format("%02X ", x);
        bufferList.add(x);
        sum += 1;
      }
    }

    LOGGER.debug("iterCount: {}", iterCount);
    LOGGER.debug("Sum: {}", sum);
    LOGGER.debug("bufferList: {}", bufferList.size());
    LOGGER.debug("hex: {}", hex);

    byte[] result = new byte[bufferList.size()];
    for (int i = 0; i < bufferList.size(); i++) {
      result[i] = bufferList.get(i).byteValue();
    }
    return result;
  }
}
