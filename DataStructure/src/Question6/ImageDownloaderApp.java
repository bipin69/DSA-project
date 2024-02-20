package Question6;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

class DownloadModel {
    private final String url;
    private final AtomicBoolean paused = new AtomicBoolean(false);
    private volatile String status = "Waiting...";
    private volatile long totalBytes = 0L;
    private volatile long downloadedBytes = 0L;
    private Future<?> future;

    public DownloadModel(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public synchronized boolean isPaused() {
        return paused.get();
    }

    public synchronized void pause() {
        paused.set(true);
    }

    public synchronized void resume() {
        paused.set(false);
        notifyAll();
    }

    public String getStatus() {
        return status;
    }

    public synchronized void setStatus(String status) {
        this.status = status;
    }

    public void setFuture(Future<?> future) {
        this.future = future;
    }

    public void cancel() {
        if (future != null)
            future.cancel(true);
    }



    public synchronized void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    public synchronized void addDownloadedBytes(long bytes) {
        this.downloadedBytes += bytes;
    }

    public long getDownloadedBytes() {
        return downloadedBytes;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public String getFormattedFileName() {
        String fileName = new File(url).getName();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());
        return timestamp + "_" + fileName;
    }
}

class DownloadTask implements Callable<Void> {
    private final DownloadModel model;
    private final Runnable updateUI;
    private static final int DOWNLOAD_SPEED = 500; // Adjust the download speed (in milliseconds)

    public DownloadTask(DownloadModel model, Runnable updateUI) {
        this.model = model;
        this.updateUI = updateUI;
    }

    @Override
    public Void call() throws Exception {
        model.setStatus("Downloading");
        URL url = new URL(model.getUrl());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        long fileSize = connection.getContentLengthLong();
        model.setTotalBytes(fileSize);

        try (InputStream in = new BufferedInputStream(connection.getInputStream())) {
            Path targetPath = Paths.get("downloads", model.getFormattedFileName());
            Files.createDirectories(targetPath.getParent());
            try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(targetPath))) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    synchronized (model) {
                        while (model.isPaused())
                            model.wait();
                    }
                    out.write(buffer, 0, bytesRead);
                    model.addDownloadedBytes(bytesRead);
                    updateUI.run();
                    // Introduce a delay to slow down the download speed
                    Thread.sleep(DOWNLOAD_SPEED);
                }
                model.setStatus("Completed");
            }
        } catch (IOException | InterruptedException e) {
            model.setStatus("Error: " + e.getMessage());
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        } finally {
            updateUI.run();
        }
        return null;
    }
}


class DownloadListCellRenderer extends JPanel implements ListCellRenderer<DownloadModel> {
//    private JButton pauseResumeButton = new JButton("Pause/Resume");
//    private JButton cancelButton = new JButton("Cancel");
    private JList<? extends DownloadModel> downloadList;

    public DownloadListCellRenderer(JList<? extends DownloadModel> downloadList) {
        this.downloadList = downloadList;

//        setupButtonActions();
//        styleButtons();

        setLayout(new BorderLayout());
//        add(pauseResumeButton, BorderLayout.WEST);
//        add(cancelButton, BorderLayout.EAST);
    }

//    private void setupButtonActions() {
//        pauseResumeButton.addActionListener(e -> {
//            DownloadModel selectedModel = downloadList.getSelectedValue();
//            if (selectedModel != null) {
//                if (selectedModel.isPaused()) {
//                    selectedModel.resume();
//                } else {
//                    selectedModel.pause();
//                }
//                downloadList.repaint();
//            }
//        });
//
//        cancelButton.addActionListener(e -> {
//            DownloadModel selectedModel = downloadList.getSelectedValue();
//            if (selectedModel != null) {
//                selectedModel.cancel();
//                downloadList.repaint();
//            }
//        });
//    }

//    private void styleButtons() {
//        pauseResumeButton.setBackground(new Color(255, 165, 0)); // Dark Orange
//        pauseResumeButton.setForeground(Color.WHITE);
//
//        cancelButton.setBackground(new Color(255, 0, 0)); // Dark Red
//        cancelButton.setForeground(Color.WHITE);
//    }

    @Override
    public Component getListCellRendererComponent(JList<? extends DownloadModel> list, DownloadModel value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        this.removeAll(); // Clear previous components
        setLayout(new BorderLayout());
        JLabel urlLabel = new JLabel(value.getUrl());
        JProgressBar progressBar = new JProgressBar(0, 100);
        if (value.getTotalBytes() > 0) {
            int progress = (int) ((value.getDownloadedBytes() * 100) / value.getTotalBytes());
            progressBar.setValue(progress);
        }
        JLabel statusLabel = new JLabel(value.getStatus());

        add(urlLabel, BorderLayout.WEST);
        add(progressBar, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.EAST);
//        add(pauseResumeButton, BorderLayout.SOUTH);
//        add(cancelButton, BorderLayout.EAST);

        if (isSelected) {
            setBackground(new Color(100, 149, 237));
            setForeground(Color.WHITE);
        } else {
            setBackground(new Color(240, 240, 240));
            setForeground(Color.BLACK);
        }
        return this;
    }
}

public class ImageDownloaderApp extends JFrame {
    private JTextField urlField = new JTextField(30);
    private JButton addButton = new JButton("Add Download");
    private DefaultListModel<DownloadModel> listModel = new DefaultListModel<>();
    private JList<DownloadModel> downloadList = new JList<>(listModel);
    private ExecutorService downloadExecutor = Executors.newFixedThreadPool(4); // 4 concurrent downloads

    public ImageDownloaderApp() {
        super("Image Downloader App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 400);
        layoutComponents();
        setVisible(true);
    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Left Panel (URL input and buttons)
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(245, 245, 245));
        urlField.setBackground(new Color(255, 255, 255));
        urlField.setForeground(Color.BLUE);
        addButton.setBackground(new Color(66, 134, 244));
        addButton.setForeground(Color.BLACK);

        // Panel for URL input and buttons (Add Download, Pause/Resume, Cancel)
        JPanel urlPanel = new JPanel(new BorderLayout());
        urlPanel.add(urlField, BorderLayout.CENTER);
        urlPanel.add(addButton, BorderLayout.EAST);

        // Panel for buttons (Pause/Resume, Cancel)
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1));
        buttonPanel.setBackground(new Color(245, 245, 245));

        JButton pauseResumeButton = new JButton("Pause/Resume");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(pauseResumeButton);
        buttonPanel.add(cancelButton);

        leftPanel.add(urlPanel, BorderLayout.NORTH); // Place the URL input and Add Download button at the top
        leftPanel.add(buttonPanel, BorderLayout.SOUTH); // Add buttons below the URL input

        // Center Panel (Download list)
        JScrollPane scrollPane = new JScrollPane(downloadList);
        downloadList.setCellRenderer(new DownloadListCellRenderer(downloadList));
        scrollPane.setBackground(new Color(240, 240, 240));
        downloadList.setBackground(new Color(240, 240, 240));
        downloadList.setForeground(Color.BLACK);

        // Right Panel (Empty)
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(new Color(245, 245, 245));

        // Main Panel (Combining left, center, and right panels)
        mainPanel.add(leftPanel, BorderLayout.WEST); // Placing left panel on the left side
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        // Action listener for the "Add Download" button
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addDownload(urlField.getText().trim());
            }
        });

        // Action listeners for Pause/Resume and Cancel buttons
        pauseResumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DownloadModel selectedModel = downloadList.getSelectedValue();
                if (selectedModel != null) {
                    if (selectedModel.isPaused()) {
                        selectedModel.resume();
                    } else {
                        selectedModel.pause();
                    }
                    downloadList.repaint();
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DownloadModel selectedModel = downloadList.getSelectedValue();
                if (selectedModel != null) {
                    selectedModel.cancel();
                    listModel.removeElement(selectedModel);
                    downloadList.repaint();
                }
            }
        });
    }

    private void addDownload(String url) {
        try {
            new URL(url);
            DownloadModel model = new DownloadModel(url);
            listModel.addElement(model);
            DownloadTask task = new DownloadTask(model, () -> SwingUtilities.invokeLater(this::repaint));
            model.setFuture(downloadExecutor.submit(task));
        } catch (MalformedURLException ex) {
            JOptionPane.showMessageDialog(this, " URL is empty: " + url, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ImageDownloaderApp::new);
    }
}
