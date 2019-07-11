package SJmp3;

import SJmp3.Mixer.MixerFrame;
import SJmp3.Mixer.ControlSound;
import SJmp3.ClipBoard.ClipboardTextTransfer;
import SJmp3.LoadSave.SavePlayList;
import SJmp3.Threads.Next_Thread;
//import SJmp3.Threads.WavToMp3_Thread;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class SJmp3gui extends javax.swing.JFrame {

    public SJmp3gui() {
        initComponents();
        this.top="SJmp3 - Small Java mp3 player, maven build: 15-10-18";
        this.setTitle(top);
        //LST1.setComponentPopupMenu(mPopup);
        FrameIcon = new ImageIcon(getClass().getResource("/SJmp3/img/SubFrameIcon.png"));
        this.setIconImage(FrameIcon.getImage());
    }

    public static SJmp3gui frame;
    public static String top;
    public static JList currentLST;
    public static List<String> lookAndFeelsDisplay = new ArrayList<>();
    public static List<String> lookAndFeelsRealNames = new ArrayList<>();
    public JPanel contentPane;
    private static final Logger j4log = Logger.getLogger(SJmp3gui.class.getName());
    public ImageIcon FrameIcon;

    public void changeLF() {
        String changeLook = (String) JOptionPane.showInputDialog(frame, "Choose Look and Feel Here:", "Select Look and Feel", JOptionPane.QUESTION_MESSAGE, new ImageIcon(getClass().getResource("/SJmp3/img/color_swatch.png")), lookAndFeelsDisplay.toArray(), null);
        if (changeLook != null) {
            for (int a = 0; a < lookAndFeelsDisplay.size(); a++) {
                if (changeLook.equals(lookAndFeelsDisplay.get(a))) {
                    Actions.currentLAF = lookAndFeelsRealNames.get(a);
                    setLF(frame);
                    break;
                }
            }
        }
    }
    
    public void showClipboard(JFrame frame) {
        ClipboardTextTransfer textTransfer = new ClipboardTextTransfer();
        String buf = textTransfer.getClipboardContents();
        JTextArea taClip = new JTextArea(10,50);
        taClip.setText(buf);
        //taClip.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        JScrollPane jsc=new JScrollPane(taClip);
        jsc.setViewportView(taClip);        
        Object[] ob = {jsc};
        ImageIcon icon = new ImageIcon(getClass().getResource("/SJmp3/img/24x24/clipboard_search.png"));
        JOptionPane.showMessageDialog(frame, ob, "ClipBoard", JOptionPane.INFORMATION_MESSAGE, icon);
    }    
    
    public void about(JFrame frame) {
        ImageIcon icon = new ImageIcon(getClass().getResource("/SJmp3/img/sjmp3_main.png"));
        JOptionPane.showMessageDialog(frame,
            "SJmp3 - free portable cross-platform Small Java mp3 player. \n"+
            "Supported formats: *.mp1 , *.mp2 , *.mp3 , *.mpga. \n" + 
            "Successfully tested on Windows and Linux OS.\n"+ 
            "Home = https://sourceforge.net/projects/sjmp3/ \n" +
            "or https://github.com/harp077/sjmp3\n" +
            "Roman Koldaev, Saratov city, Russia,\n" +                    
            "Mail = harp07@mail.ru \n" + 
            "Need JRE 1.8.\n", 
            top, JOptionPane.INFORMATION_MESSAGE, icon);
    }    

    public void ModeSelectShuffle() {
        mLoop.setSelected(false);
        mShuffle.setSelected(true);
        mRepeat.setSelected(false);
        mClassic.setSelected(false);
        bMode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/media_playlist_shuffle.png")));
        bMode.setToolTipText("Current Mode is Shuffle (Random select)");
        Actions.currentMode = "shuffle";
    }

    public void ModeSelectLoop() {
        mLoop.setSelected(true);
        mShuffle.setSelected(false);
        mRepeat.setSelected(false);
        mClassic.setSelected(false);
        bMode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/loop-orange.png")));
        bMode.setToolTipText("Current Mode is Loop (Repeat List)");
        Actions.currentMode = "loop";
    }

    public void ModeSelectRepeat() {
        mLoop.setSelected(false);
        mShuffle.setSelected(false);
        mRepeat.setSelected(true);
        mClassic.setSelected(false);
        bMode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/One-24.png")));
        bMode.setToolTipText("Current Mode is Repeat Track");
        Actions.currentMode = "repeat";
    }

    public void ModeSelect() {
        switch (Actions.currentMode) {
            case "classic":
                Actions.currentMode = "loop";
                mLoop.setSelected(true);
                mShuffle.setSelected(false);
                mRepeat.setSelected(false);
                mClassic.setSelected(false);
                bMode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/loop-orange.png")));
                bMode.setToolTipText("Current Mode is Loop (Repeat List)");
                break;
            case "loop":
                Actions.currentMode = "shuffle";
                mLoop.setSelected(false);
                mShuffle.setSelected(true);
                mRepeat.setSelected(false);
                mClassic.setSelected(false);
                bMode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/media_playlist_shuffle.png")));
                bMode.setToolTipText("Current Mode is Shuffle (Random select)");
                break;
            case "shuffle":
                Actions.currentMode = "repeat";
                mLoop.setSelected(false);
                mShuffle.setSelected(false);
                mRepeat.setSelected(true);
                mClassic.setSelected(false);
                bMode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/One-24.png")));
                bMode.setToolTipText("Current Mode is Repeat Track");
                break;
            case "repeat":
                Actions.currentMode = "classic";
                mLoop.setSelected(false);
                mShuffle.setSelected(false);
                mRepeat.setSelected(false);
                mClassic.setSelected(true);
                bMode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/next-forward.png")));
                bMode.setToolTipText("Current Mode is No Repeat");
                break;
        }
    }

    public void MixerInit() {
        if ((Actions.currentMixer > 0) & (Actions.currentMixer < 100 / 3)) {
            SJmp3gui.bSoundMixer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/audio_volume_low.png")));
        }
        if ((Actions.currentMixer > 100 / 3) & (Actions.currentMixer < 200 / 3)) {
            SJmp3gui.bSoundMixer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/audio_volume_medium.png")));
        }
        if (Actions.currentMixer > 200 / 3) {
            SJmp3gui.bSoundMixer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/audio_volume_high.png")));
        }
        if (Actions.currentMixer == 0) {
            SJmp3gui.bSoundMixer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/audio_volume_muted.png")));
        }
        if (Actions.currentMute.equals("true")) {
            ControlSound.setMasterOutputMute(true);
            //mf.bMute.setSelected(true);
            System.out.println("Volume set to ZERO = 0");
            //mf.MixerFrameSlider.setEnabled(false);
            SJmp3gui.bSoundMixer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/audio_volume_muted.png")));
        }
        try {
            ControlSound.setMasterOutputVolume((float) Actions.currentMixer / 100);
        } catch (RuntimeException rr) {
            System.out.println("Master output port not found");
        }
    }

    public void MixerSet() {
        MixerFrame mf = new MixerFrame(frame, true);
        mf.setLocationRelativeTo(TimeTFend);
        mf.setVisible(true);
        MixerFrame.MixerFrameSlider.setValue(Actions.currentMixer);
        MixerInit();
    }

    public void setLF(JFrame frame) {
        try {
            UIManager.setLookAndFeel(Actions.currentLAF);
        } catch (InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(this.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SJmp3gui.class.getName()).log(Level.SEVERE, null, ex);
        }
        SwingUtilities.updateComponentTreeUI(frame);
        //frame.pack();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tbVerh = new javax.swing.JToolBar();
        jSeparator16 = new javax.swing.JToolBar.Separator();
        tbOpen = new javax.swing.JToolBar();
        bOpenFile = new javax.swing.JButton();
        bOpenFolder = new javax.swing.JButton();
        bAddList = new javax.swing.JButton();
        jSeparator14 = new javax.swing.JToolBar.Separator();
        tbListEditor = new javax.swing.JToolBar();
        bSongUP = new javax.swing.JButton();
        bCopyToClipboard = new javax.swing.JButton();
        bPasteFromClipboard = new javax.swing.JButton();
        bSavePL = new javax.swing.JButton();
        bRemoveSong = new javax.swing.JButton();
        bRemoveList = new javax.swing.JButton();
        bSongDown = new javax.swing.JButton();
        jSeparator15 = new javax.swing.JToolBar.Separator();
        tbInfo = new javax.swing.JToolBar();
        bShowClipboard = new javax.swing.JButton();
        bid3Info1 = new javax.swing.JButton();
        bAbout = new javax.swing.JButton();
        jSeparator17 = new javax.swing.JToolBar.Separator();
        jScrollPane1 = new javax.swing.JScrollPane();
        LST1 = new javax.swing.JList();
        jToolBar2 = new javax.swing.JToolBar();
        SongSlider = new javax.swing.JSlider();
        jSeparator13 = new javax.swing.JToolBar.Separator();
        tbNiz = new javax.swing.JToolBar();
        TimeTFcur = new javax.swing.JTextField();
        tbOptions = new javax.swing.JToolBar();
        bChangeSkin = new javax.swing.JButton();
        bMode = new javax.swing.JButton();
        tbTrackActions = new javax.swing.JToolBar();
        bPrevious = new javax.swing.JButton();
        bFastBackward = new javax.swing.JButton();
        bPause = new javax.swing.JButton();
        bPlay = new javax.swing.JButton();
        bStop = new javax.swing.JButton();
        bFastForward = new javax.swing.JButton();
        bNext = new javax.swing.JButton();
        jToolBar1 = new javax.swing.JToolBar();
        bTrackInfo = new javax.swing.JButton();
        bSoundMixer = new javax.swing.JButton();
        TimeTFend = new javax.swing.JTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        File = new javax.swing.JMenu();
        mFileOpen = new javax.swing.JMenuItem();
        mFolderOpen = new javax.swing.JMenuItem();
        mListAdd = new javax.swing.JMenuItem();
        mQuit = new javax.swing.JMenuItem();
        Action = new javax.swing.JMenu();
        mPlay = new javax.swing.JMenuItem();
        mPause = new javax.swing.JMenuItem();
        mStop = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        mNext = new javax.swing.JMenuItem();
        mPrevious = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        mFastForward = new javax.swing.JMenuItem();
        mFastBackward = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        mid3info1 = new javax.swing.JMenuItem();
        mRepeatTrack = new javax.swing.JMenuItem();
        mListEdit = new javax.swing.JMenu();
        mSongUp = new javax.swing.JMenuItem();
        mSongDown = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mCopyPathClipboard = new javax.swing.JMenuItem();
        mPasteClipboard = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        mRemoveSong = new javax.swing.JMenuItem();
        mRemoveAll = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        mSavePlayList = new javax.swing.JMenuItem();
        mOptions = new javax.swing.JMenu();
        jMenu6 = new javax.swing.JMenu();
        mClassic = new javax.swing.JRadioButtonMenuItem();
        mLoop = new javax.swing.JRadioButtonMenuItem();
        mShuffle = new javax.swing.JRadioButtonMenuItem();
        mRepeat = new javax.swing.JRadioButtonMenuItem();
        mSoundVolume = new javax.swing.JMenuItem();
        mChangeLF = new javax.swing.JMenuItem();
        Help = new javax.swing.JMenu();
        mShowClipboard = new javax.swing.JMenuItem();
        mid3info = new javax.swing.JMenuItem();
        mAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SJmp3_03-10-15");
        setLocation(new java.awt.Point(100, 100));
        setUndecorated(true);
        setResizable(false);
        setSize(new java.awt.Dimension(504, 381));
        getContentPane().setLayout(new java.awt.BorderLayout(5, 5));

        tbVerh.setFloatable(false);
        tbVerh.add(jSeparator16);

        tbOpen.setBorder(javax.swing.BorderFactory.createTitledBorder("Open"));
        tbOpen.setFloatable(false);
        tbOpen.setRollover(true);

        bOpenFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/Actions-document-open-icon.png"))); // NOI18N
        bOpenFile.setToolTipText("Open File");
        bOpenFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bOpenFileActionPerformed(evt);
            }
        });
        tbOpen.add(bOpenFile);

        bOpenFolder.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/folder-light-green.png"))); // NOI18N
        bOpenFolder.setToolTipText("Open Folder");
        bOpenFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bOpenFolderActionPerformed(evt);
            }
        });
        tbOpen.add(bOpenFolder);

        bAddList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/audio_x_generic.png"))); // NOI18N
        bAddList.setToolTipText("Load PlayList");
        bAddList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bAddListActionPerformed(evt);
            }
        });
        tbOpen.add(bAddList);

        tbVerh.add(tbOpen);
        tbVerh.add(jSeparator14);

        tbListEditor.setBorder(javax.swing.BorderFactory.createTitledBorder("List Editor"));
        tbListEditor.setFloatable(false);
        tbListEditor.setRollover(true);

        bSongUP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/up-cyan-24.png"))); // NOI18N
        bSongUP.setToolTipText("Move Track Up");
        bSongUP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSongUPActionPerformed(evt);
            }
        });
        tbListEditor.add(bSongUP);

        bCopyToClipboard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/copy-blue-24.png"))); // NOI18N
        bCopyToClipboard.setToolTipText("Copy selected Path/URL from List to Clipboard");
        bCopyToClipboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bCopyToClipboardActionPerformed(evt);
            }
        });
        tbListEditor.add(bCopyToClipboard);

        bPasteFromClipboard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/paste-24-1.png"))); // NOI18N
        bPasteFromClipboard.setToolTipText("Paste Path/URL from Clipboard to List");
        bPasteFromClipboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bPasteFromClipboardActionPerformed(evt);
            }
        });
        tbListEditor.add(bPasteFromClipboard);

        bSavePL.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/playlist-save.png"))); // NOI18N
        bSavePL.setToolTipText("Save PlayList");
        bSavePL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSavePLActionPerformed(evt);
            }
        });
        tbListEditor.add(bSavePL);

        bRemoveSong.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/list_remove-24.png"))); // NOI18N
        bRemoveSong.setToolTipText("Remove Track from List");
        bRemoveSong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRemoveSongActionPerformed(evt);
            }
        });
        tbListEditor.add(bRemoveSong);

        bRemoveList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/edit_clear.png"))); // NOI18N
        bRemoveList.setToolTipText("Remove All from List");
        bRemoveList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRemoveListActionPerformed(evt);
            }
        });
        tbListEditor.add(bRemoveList);

        bSongDown.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/down-cyan-24.png"))); // NOI18N
        bSongDown.setToolTipText("Move Track Down");
        bSongDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSongDownActionPerformed(evt);
            }
        });
        tbListEditor.add(bSongDown);

        tbVerh.add(tbListEditor);
        tbVerh.add(jSeparator15);

        tbInfo.setBorder(javax.swing.BorderFactory.createTitledBorder("Info"));
        tbInfo.setFloatable(false);
        tbInfo.setRollover(true);
        tbInfo.setToolTipText("Info");

        bShowClipboard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/clipboard_lupa-24.png"))); // NOI18N
        bShowClipboard.setToolTipText("Show Clipboard");
        bShowClipboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bShowClipboardActionPerformed(evt);
            }
        });
        tbInfo.add(bShowClipboard);

        bid3Info1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/info-cyan-24.png"))); // NOI18N
        bid3Info1.setToolTipText("Track Info");
        bid3Info1.setFocusable(false);
        bid3Info1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bid3Info1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bid3Info1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bid3Info1ActionPerformed(evt);
            }
        });
        tbInfo.add(bid3Info1);

        bAbout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/help-cyan-24.png"))); // NOI18N
        bAbout.setToolTipText("About");
        bAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bAboutActionPerformed(evt);
            }
        });
        tbInfo.add(bAbout);

        tbVerh.add(tbInfo);
        tbVerh.add(jSeparator17);

        getContentPane().add(tbVerh, java.awt.BorderLayout.NORTH);

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("PlayList"));

        LST1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        LST1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                LST1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(LST1);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jToolBar2.setFloatable(false);
        jToolBar2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        SongSlider.setMaximum(1000);
        SongSlider.setValue(0);
        SongSlider.setMaximumSize(new java.awt.Dimension(500, 19));
        SongSlider.setMinimumSize(new java.awt.Dimension(500, 19));
        SongSlider.setPreferredSize(new java.awt.Dimension(500, 19));
        SongSlider.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                SongSliderMouseDragged(evt);
            }
        });
        SongSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                SongSliderMouseClicked(evt);
            }
        });
        jToolBar2.add(SongSlider);
        jToolBar2.add(jSeparator13);

        tbNiz.setFloatable(false);

        TimeTFcur.setEditable(false);
        TimeTFcur.setMaximumSize(new java.awt.Dimension(50, 19));
        TimeTFcur.setMinimumSize(new java.awt.Dimension(50, 19));
        TimeTFcur.setPreferredSize(new java.awt.Dimension(50, 19));
        tbNiz.add(TimeTFcur);

        tbOptions.setBorder(javax.swing.BorderFactory.createTitledBorder("Options"));
        tbOptions.setFloatable(false);
        tbOptions.setRollover(true);
        tbOptions.setToolTipText("Options");

        bChangeSkin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/skin_color_chooser-24.png"))); // NOI18N
        bChangeSkin.setToolTipText("Change Skin");
        bChangeSkin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bChangeSkinActionPerformed(evt);
            }
        });
        tbOptions.add(bChangeSkin);

        bMode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/next-forward.png"))); // NOI18N
        bMode.setToolTipText("Mode");
        bMode.setFocusable(false);
        bMode.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bMode.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bModeActionPerformed(evt);
            }
        });
        tbOptions.add(bMode);

        tbNiz.add(tbOptions);

        tbTrackActions.setBorder(javax.swing.BorderFactory.createTitledBorder("Track Actions"));
        tbTrackActions.setFloatable(false);
        tbTrackActions.setRollover(true);

        bPrevious.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/old_go_previous.png"))); // NOI18N
        bPrevious.setToolTipText("Previous Track");
        bPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bPreviousActionPerformed(evt);
            }
        });
        tbTrackActions.add(bPrevious);

        bFastBackward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/Actions-arrow-left-double-icon.png"))); // NOI18N
        bFastBackward.setToolTipText("Fast Backword");
        bFastBackward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bFastBackwardActionPerformed(evt);
            }
        });
        tbTrackActions.add(bFastBackward);

        bPause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/pause-orange.png"))); // NOI18N
        bPause.setToolTipText("Pause");
        bPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bPauseActionPerformed(evt);
            }
        });
        tbTrackActions.add(bPause);

        bPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/play1hot.png"))); // NOI18N
        bPlay.setToolTipText("Play");
        bPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bPlayActionPerformed(evt);
            }
        });
        tbTrackActions.add(bPlay);

        bStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/stop-red.png"))); // NOI18N
        bStop.setToolTipText("Stop");
        bStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bStopActionPerformed(evt);
            }
        });
        tbTrackActions.add(bStop);

        bFastForward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/Actions-arrow-right-double-icon.png"))); // NOI18N
        bFastForward.setToolTipText("Fast Forward");
        bFastForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bFastForwardActionPerformed(evt);
            }
        });
        tbTrackActions.add(bFastForward);

        bNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/old_go_next.png"))); // NOI18N
        bNext.setToolTipText("Next Track");
        bNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bNextActionPerformed(evt);
            }
        });
        tbTrackActions.add(bNext);

        tbNiz.add(tbTrackActions);

        jToolBar1.setBorder(javax.swing.BorderFactory.createTitledBorder("Tools"));
        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        bTrackInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/info-cyan-24.png"))); // NOI18N
        bTrackInfo.setToolTipText("Track Info");
        bTrackInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bTrackInfoActionPerformed(evt);
            }
        });
        jToolBar1.add(bTrackInfo);

        bSoundMixer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/audio_volume_high.png"))); // NOI18N
        bSoundMixer.setToolTipText("Sound Volume");
        bSoundMixer.setFocusable(false);
        bSoundMixer.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bSoundMixer.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bSoundMixer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSoundMixerActionPerformed(evt);
            }
        });
        jToolBar1.add(bSoundMixer);

        tbNiz.add(jToolBar1);

        TimeTFend.setEditable(false);
        TimeTFend.setMaximumSize(new java.awt.Dimension(50, 19));
        TimeTFend.setMinimumSize(new java.awt.Dimension(50, 19));
        TimeTFend.setPreferredSize(new java.awt.Dimension(50, 19));
        tbNiz.add(TimeTFend);

        jToolBar2.add(tbNiz);

        getContentPane().add(jToolBar2, java.awt.BorderLayout.SOUTH);

        jMenuBar1.setToolTipText("");

        File.setText("File");

        mFileOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/16x16/go-up.png"))); // NOI18N
        mFileOpen.setText("Open File");
        mFileOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mFileOpenActionPerformed(evt);
            }
        });
        File.add(mFileOpen);

        mFolderOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/16x16/open-folder-plus-16.png"))); // NOI18N
        mFolderOpen.setText("Open Folder");
        mFolderOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mFolderOpenActionPerformed(evt);
            }
        });
        File.add(mFolderOpen);

        mListAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/16x16/folder_open_document_music_playlist.png"))); // NOI18N
        mListAdd.setText("Load PlayList");
        mListAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mListAddActionPerformed(evt);
            }
        });
        File.add(mListAdd);

        mQuit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/16x16/quit.png"))); // NOI18N
        mQuit.setText("Exit");
        mQuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mQuitActionPerformed(evt);
            }
        });
        File.add(mQuit);

        jMenuBar1.add(File);

        Action.setText("Track Actions");

        mPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/16x16/play_green-1.png"))); // NOI18N
        mPlay.setText("Play");
        mPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mPlayActionPerformed(evt);
            }
        });
        Action.add(mPlay);

        mPause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/16x16/control_pause.png"))); // NOI18N
        mPause.setText("Pause");
        mPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mPauseActionPerformed(evt);
            }
        });
        Action.add(mPause);

        mStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/16x16/stop_red.png"))); // NOI18N
        mStop.setText("Stop");
        mStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mStopActionPerformed(evt);
            }
        });
        Action.add(mStop);
        Action.add(jSeparator4);

        mNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/16x16/go-next.png"))); // NOI18N
        mNext.setText("Next Track");
        mNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mNextActionPerformed(evt);
            }
        });
        Action.add(mNext);

        mPrevious.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/16x16/go-previous.png"))); // NOI18N
        mPrevious.setText("Previous Track");
        mPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mPreviousActionPerformed(evt);
            }
        });
        Action.add(mPrevious);
        Action.add(jSeparator5);

        mFastForward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/16x16/fast-forward.png"))); // NOI18N
        mFastForward.setText("Fast Forward");
        mFastForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mFastForwardActionPerformed(evt);
            }
        });
        Action.add(mFastForward);

        mFastBackward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/16x16/fast-backward.png"))); // NOI18N
        mFastBackward.setText("Fast Backward");
        mFastBackward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mFastBackwardActionPerformed(evt);
            }
        });
        Action.add(mFastBackward);
        Action.add(jSeparator6);

        mid3info1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/16x16/info-cyan-16.png"))); // NOI18N
        mid3info1.setText("File/Stream Info");
        mid3info1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mid3info1ActionPerformed(evt);
            }
        });
        Action.add(mid3info1);

        mRepeatTrack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/16x16/repeat_track.png"))); // NOI18N
        mRepeatTrack.setText("Repeat Track Mode");
        mRepeatTrack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mRepeatTrackActionPerformed(evt);
            }
        });
        Action.add(mRepeatTrack);

        jMenuBar1.add(Action);

        mListEdit.setText("List Editor");

        mSongUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/16x16/up-cyan-16.png"))); // NOI18N
        mSongUp.setText("Move Track Up");
        mSongUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mSongUpActionPerformed(evt);
            }
        });
        mListEdit.add(mSongUp);

        mSongDown.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/16x16/down-cyan-16.png"))); // NOI18N
        mSongDown.setText("Move Track Down");
        mSongDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mSongDownActionPerformed(evt);
            }
        });
        mListEdit.add(mSongDown);
        mListEdit.add(jSeparator1);

        mCopyPathClipboard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/16x16/clipboard_plus.png"))); // NOI18N
        mCopyPathClipboard.setText("Copy Path/URL to Clipboard");
        mCopyPathClipboard.setToolTipText("Copy selected Path/URL from List to Clipboard");
        mCopyPathClipboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mCopyPathClipboardActionPerformed(evt);
            }
        });
        mListEdit.add(mCopyPathClipboard);

        mPasteClipboard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/16x16/clipboard_paste.png"))); // NOI18N
        mPasteClipboard.setText("Paste Path/URL from Clipboard");
        mPasteClipboard.setToolTipText("Paste Path/URL from Clipboard to List");
        mPasteClipboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mPasteClipboardActionPerformed(evt);
            }
        });
        mListEdit.add(mPasteClipboard);
        mListEdit.add(jSeparator2);

        mRemoveSong.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/16x16/list_remove-16.png"))); // NOI18N
        mRemoveSong.setText("Remove Track from List");
        mRemoveSong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mRemoveSongActionPerformed(evt);
            }
        });
        mListEdit.add(mRemoveSong);

        mRemoveAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/16x16/recycler.png"))); // NOI18N
        mRemoveAll.setText("Remove All from List");
        mRemoveAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mRemoveAllActionPerformed(evt);
            }
        });
        mListEdit.add(mRemoveAll);
        mListEdit.add(jSeparator3);

        mSavePlayList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/16x16/save.png"))); // NOI18N
        mSavePlayList.setText("Save PlayList");
        mSavePlayList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mSavePlayListActionPerformed(evt);
            }
        });
        mListEdit.add(mSavePlayList);

        jMenuBar1.add(mListEdit);

        mOptions.setText("Options");

        jMenu6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/16x16/select-by-color-icon.png"))); // NOI18N
        jMenu6.setText("Mode Select");

        mClassic.setText("No Repeat");
        mClassic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/16x16/next-forward-16.png"))); // NOI18N
        mClassic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mClassicActionPerformed(evt);
            }
        });
        jMenu6.add(mClassic);

        mLoop.setText("Loop ");
        mLoop.setToolTipText("Repeat List");
        mLoop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/16x16/media-playlist-repeat_all.png"))); // NOI18N
        mLoop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mLoopActionPerformed(evt);
            }
        });
        jMenu6.add(mLoop);

        mShuffle.setText("Shuffle");
        mShuffle.setToolTipText("Random Mode");
        mShuffle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/16x16/shuffle-16.png"))); // NOI18N
        mShuffle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mShuffleActionPerformed(evt);
            }
        });
        jMenu6.add(mShuffle);

        mRepeat.setText("Repeat Track");
        mRepeat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/16x16/repeat_track.png"))); // NOI18N
        mRepeat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mRepeatActionPerformed(evt);
            }
        });
        jMenu6.add(mRepeat);

        mOptions.add(jMenu6);

        mSoundVolume.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/16x16/volume_loud.png"))); // NOI18N
        mSoundVolume.setText("Sound Volume");
        mSoundVolume.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mSoundVolumeActionPerformed(evt);
            }
        });
        mOptions.add(mSoundVolume);

        mChangeLF.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/16x16/color_swatches.png"))); // NOI18N
        mChangeLF.setText("Change Skin");
        mChangeLF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mChangeLFActionPerformed(evt);
            }
        });
        mOptions.add(mChangeLF);

        jMenuBar1.add(mOptions);

        Help.setText("Info");

        mShowClipboard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/16x16/clipboard_lupa-16.png"))); // NOI18N
        mShowClipboard.setText("Show Clipboard");
        mShowClipboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mShowClipboardActionPerformed(evt);
            }
        });
        Help.add(mShowClipboard);

        mid3info.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/16x16/info-cyan-16.png"))); // NOI18N
        mid3info.setText("Track Info");
        mid3info.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mid3infoActionPerformed(evt);
            }
        });
        Help.add(mid3info);

        mAbout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/16x16/help-cyan-16.png"))); // NOI18N
        mAbout.setText("About");
        mAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mAboutActionPerformed(evt);
            }
        });
        Help.add(mAbout);

        jMenuBar1.add(Help);

        setJMenuBar(jMenuBar1);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
    private void bOpenFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bOpenFileActionPerformed
        Actions.SelectFile();
    }//GEN-LAST:event_bOpenFileActionPerformed
    private void bPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bPlayActionPerformed
        Actions.GoPlay();
    }//GEN-LAST:event_bPlayActionPerformed
    private void mAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mAboutActionPerformed
        //Actions act = new Actions();
        about(this);
    }//GEN-LAST:event_mAboutActionPerformed
    private void mFileOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mFileOpenActionPerformed
        Actions.SelectFile();
    }//GEN-LAST:event_mFileOpenActionPerformed
    private void mPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mPlayActionPerformed
        Actions.GoPlay();
    }//GEN-LAST:event_mPlayActionPerformed
    private void mQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mQuitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_mQuitActionPerformed
    private void bStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bStopActionPerformed
        Actions.stopp();
    }//GEN-LAST:event_bStopActionPerformed
    private void bOpenFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bOpenFolderActionPerformed
        Actions.SelectFolder();
    }//GEN-LAST:event_bOpenFolderActionPerformed
    private void bNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bNextActionPerformed
        Actions.PlayNext();
    }//GEN-LAST:event_bNextActionPerformed
    private void bPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bPreviousActionPerformed
        Actions.PlayPrevious();
    }//GEN-LAST:event_bPreviousActionPerformed
    private void mFolderOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mFolderOpenActionPerformed
        Actions.SelectFolder();
    }//GEN-LAST:event_mFolderOpenActionPerformed
    private void mStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mStopActionPerformed
        Actions.stopp();
    }//GEN-LAST:event_mStopActionPerformed
    private void mPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mPreviousActionPerformed
        Actions.PlayPrevious();
    }//GEN-LAST:event_mPreviousActionPerformed
    private void mNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mNextActionPerformed
        Actions.PlayNext();
    }//GEN-LAST:event_mNextActionPerformed
    private void bRemoveListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRemoveListActionPerformed
        Actions.RemoveAll(frame);
    }//GEN-LAST:event_bRemoveListActionPerformed
    private void bAddListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAddListActionPerformed
        Actions.AddList();
    }//GEN-LAST:event_bAddListActionPerformed
    private void bTrackInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bTrackInfoActionPerformed
        Actions act = new Actions();
        act.mp3info();
    }//GEN-LAST:event_bTrackInfoActionPerformed
    private void mListAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mListAddActionPerformed
        Actions.AddList();
    }//GEN-LAST:event_mListAddActionPerformed
    private void bPauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bPauseActionPerformed
        Actions.pause();
    }//GEN-LAST:event_bPauseActionPerformed
    private void mPauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mPauseActionPerformed
        Actions.pause();
    }//GEN-LAST:event_mPauseActionPerformed
    private void mid3infoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mid3infoActionPerformed
        Actions act = new Actions();
        act.mp3info();
    }//GEN-LAST:event_mid3infoActionPerformed
    private void bSavePLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSavePLActionPerformed
        SavePlayList.Save();
    }//GEN-LAST:event_bSavePLActionPerformed
    private void bFastForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bFastForwardActionPerformed
        Actions.FastForward();
    }//GEN-LAST:event_bFastForwardActionPerformed
    private void bFastBackwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bFastBackwardActionPerformed
        Actions.FastBackward();
    }//GEN-LAST:event_bFastBackwardActionPerformed
    private void mFastForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mFastForwardActionPerformed
        Actions.FastForward();
    }//GEN-LAST:event_mFastForwardActionPerformed
    private void mFastBackwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mFastBackwardActionPerformed
        Actions.FastBackward();
    }//GEN-LAST:event_mFastBackwardActionPerformed
    private void bSoundMixerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSoundMixerActionPerformed
        MixerSet();
    }//GEN-LAST:event_bSoundMixerActionPerformed
    private void mSoundVolumeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mSoundVolumeActionPerformed
        MixerSet();
    }//GEN-LAST:event_mSoundVolumeActionPerformed
    private void mSavePlayListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mSavePlayListActionPerformed
        SavePlayList.Save();
    }//GEN-LAST:event_mSavePlayListActionPerformed
    private void mRemoveAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mRemoveAllActionPerformed
        Actions.RemoveAll(frame);
    }//GEN-LAST:event_mRemoveAllActionPerformed
    private void mRemoveSongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mRemoveSongActionPerformed
        Actions.RemoveSong(frame);
    }//GEN-LAST:event_mRemoveSongActionPerformed
    private void bRemoveSongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRemoveSongActionPerformed
        Actions.RemoveSong(frame);
    }//GEN-LAST:event_bRemoveSongActionPerformed
    private void bSongDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSongDownActionPerformed
        Actions.SongDown();
    }//GEN-LAST:event_bSongDownActionPerformed
    private void bSongUPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSongUPActionPerformed
        Actions.SongUp();
    }//GEN-LAST:event_bSongUPActionPerformed
    private void mSongUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mSongUpActionPerformed
        Actions.SongUp();
    }//GEN-LAST:event_mSongUpActionPerformed
    private void mSongDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mSongDownActionPerformed
        Actions.SongDown();
    }//GEN-LAST:event_mSongDownActionPerformed
    private void SongSliderMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SongSliderMouseDragged
        Actions.SliderMouse();
    }//GEN-LAST:event_SongSliderMouseDragged
    private void SongSliderMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SongSliderMouseClicked
        Actions.SliderMouse();
    }//GEN-LAST:event_SongSliderMouseClicked
    private void LST1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LST1MouseClicked
        if (evt.getButton() == MouseEvent.BUTTON1) {
            Actions.LSTmouseClick();
        }
        if (evt.getClickCount() == 2 && !evt.isConsumed()) {
            evt.consume();
            System.out.println("Double Click");
            Actions.GoPlay();
        }
    }//GEN-LAST:event_LST1MouseClicked
    private void mClassicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mClassicActionPerformed
        mLoop.setSelected(false);
        mShuffle.setSelected(false);
        mRepeat.setSelected(false);
        mClassic.setSelected(true);
        bMode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/next-forward.png")));
        bMode.setToolTipText("Current Mode is No Repeat");
        Actions.currentMode = "classic";
    }//GEN-LAST:event_mClassicActionPerformed
    private void mLoopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mLoopActionPerformed
        ModeSelectLoop();
    }//GEN-LAST:event_mLoopActionPerformed
    private void mShuffleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mShuffleActionPerformed
        ModeSelectShuffle();
    }//GEN-LAST:event_mShuffleActionPerformed
    private void mRepeatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mRepeatActionPerformed
        ModeSelectRepeat();
    }//GEN-LAST:event_mRepeatActionPerformed
    private void mCopyPathClipboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mCopyPathClipboardActionPerformed
        Actions.CopyPathURLtoClipboard();
    }//GEN-LAST:event_mCopyPathClipboardActionPerformed
    private void mid3info1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mid3info1ActionPerformed
        Actions act = new Actions();
        act.mp3info();
    }//GEN-LAST:event_mid3info1ActionPerformed
    private void mRepeatTrackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mRepeatTrackActionPerformed
        ModeSelectRepeat();
    }//GEN-LAST:event_mRepeatTrackActionPerformed
    private void mPasteClipboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mPasteClipboardActionPerformed
        Actions.PastePathURLfromClipboard();
    }//GEN-LAST:event_mPasteClipboardActionPerformed
    private void bCopyToClipboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bCopyToClipboardActionPerformed
        Actions.CopyPathURLtoClipboard();
    }//GEN-LAST:event_bCopyToClipboardActionPerformed
    private void bPasteFromClipboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bPasteFromClipboardActionPerformed
        Actions.PastePathURLfromClipboard();
    }//GEN-LAST:event_bPasteFromClipboardActionPerformed
    private void mShowClipboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mShowClipboardActionPerformed
        this.showClipboard(this);
    }//GEN-LAST:event_mShowClipboardActionPerformed
    private void bModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bModeActionPerformed
        ModeSelect();
    }//GEN-LAST:event_bModeActionPerformed
    private void mChangeLFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mChangeLFActionPerformed
        changeLF();
    }//GEN-LAST:event_mChangeLFActionPerformed
    private void bChangeSkinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bChangeSkinActionPerformed
        changeLF();
    }//GEN-LAST:event_bChangeSkinActionPerformed

    private void bShowClipboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bShowClipboardActionPerformed
        this.showClipboard(this);
    }//GEN-LAST:event_bShowClipboardActionPerformed

    private void bAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAboutActionPerformed
        //Actions act = new Actions();
        this.about(frame);
    }//GEN-LAST:event_bAboutActionPerformed

    private void bid3Info1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bid3Info1ActionPerformed
        Actions act = new Actions();
        act.mp3info();
    }//GEN-LAST:event_bid3Info1ActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                frame = new SJmp3gui();
                frame.setSize(560,450);
                Actions.InstallLF();
                frame.setLF(frame);
                Actions.enableEffects();                
                //frame.setShape(new RoundRectangle2D.Double(0,0,frame.getWidth()+15,frame.getHeight()+15,15,15));
                frame.setVisible(true);
                frame.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
                //Toolkit.getDefaultToolkit().setDynamicLayout(true);
                //System.setProperty("sun.awt.noerasebackground", "true");                
                JFrame.setDefaultLookAndFeelDecorated(true);
                JDialog.setDefaultLookAndFeelDecorated(true);
                //              JMenu.setDefaultLookAndFeelDecorated(true);                
                //UIManager.put(SubstanceLookAndFeel.SHOW_EXTRA_WIDGETS, Boolean.TRUE);
                //AppCfgXerces.Load();
                //AppCfgJDOM.loadCFG();
                Actions.DefCfg();
                frame.MixerInit();
                Actions.NP = new Next_Thread();
                j4log.log(Level.INFO, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                j4log.log(Level.INFO, ControlSound.getMixers().toString());
                j4log.info(ControlSound.getHierarchyInfo());
                //j4log.warn("log4j - warn");
                switch (Actions.currentMode) {
                    case "loop":
                        mLoop.setSelected(true);
                        mShuffle.setSelected(false);
                        mRepeat.setSelected(false);
                        mClassic.setSelected(false);
                        bMode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/loop-orange.png")));
                        bMode.setToolTipText("Current Mode is Loop (Repeat List)");
                        break;
                    case "shuffle":
                        mLoop.setSelected(false);
                        mShuffle.setSelected(true);
                        mRepeat.setSelected(false);
                        mClassic.setSelected(false);
                        bMode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/media_playlist_shuffle.png")));
                        bMode.setToolTipText("Current Mode is Shuffle (Random select)");
                        break;
                    case "repeat":
                        mLoop.setSelected(false);
                        mShuffle.setSelected(false);
                        mRepeat.setSelected(true);
                        mClassic.setSelected(false);
                        bMode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/One-24.png")));
                        bMode.setToolTipText("Current Mode is Repeat Track");
                        break;
                    case "classic":
                        mLoop.setSelected(false);
                        mShuffle.setSelected(false);
                        mRepeat.setSelected(false);
                        mClassic.setSelected(true);
                        bMode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/24x24/next-forward.png")));
                        bMode.setToolTipText("Current Mode is No Repeat");
                        break;
                }
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu Action;
    private javax.swing.JMenu File;
    private javax.swing.JMenu Help;
    public static javax.swing.JList LST1;
    public static javax.swing.JSlider SongSlider;
    public static javax.swing.JTextField TimeTFcur;
    public static javax.swing.JTextField TimeTFend;
    private javax.swing.JButton bAbout;
    private javax.swing.JButton bAddList;
    private javax.swing.JButton bChangeSkin;
    private javax.swing.JButton bCopyToClipboard;
    public static javax.swing.JButton bFastBackward;
    public static javax.swing.JButton bFastForward;
    public static javax.swing.JButton bMode;
    private javax.swing.JButton bNext;
    private javax.swing.JButton bOpenFile;
    private javax.swing.JButton bOpenFolder;
    private javax.swing.JButton bPasteFromClipboard;
    public static javax.swing.JButton bPause;
    private javax.swing.JButton bPlay;
    private javax.swing.JButton bPrevious;
    private javax.swing.JButton bRemoveList;
    private javax.swing.JButton bRemoveSong;
    private javax.swing.JButton bSavePL;
    private javax.swing.JButton bShowClipboard;
    private javax.swing.JButton bSongDown;
    private javax.swing.JButton bSongUP;
    public static javax.swing.JButton bSoundMixer;
    private javax.swing.JButton bStop;
    public static javax.swing.JButton bTrackInfo;
    public static javax.swing.JButton bid3Info1;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator13;
    private javax.swing.JToolBar.Separator jSeparator14;
    private javax.swing.JToolBar.Separator jSeparator15;
    private javax.swing.JToolBar.Separator jSeparator16;
    private javax.swing.JToolBar.Separator jSeparator17;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JMenuItem mAbout;
    private javax.swing.JMenuItem mChangeLF;
    public static javax.swing.JRadioButtonMenuItem mClassic;
    private javax.swing.JMenuItem mCopyPathClipboard;
    public static javax.swing.JMenuItem mFastBackward;
    public static javax.swing.JMenuItem mFastForward;
    private javax.swing.JMenuItem mFileOpen;
    private javax.swing.JMenuItem mFolderOpen;
    private javax.swing.JMenuItem mListAdd;
    private javax.swing.JMenu mListEdit;
    public static javax.swing.JRadioButtonMenuItem mLoop;
    private javax.swing.JMenuItem mNext;
    private javax.swing.JMenu mOptions;
    private javax.swing.JMenuItem mPasteClipboard;
    public static javax.swing.JMenuItem mPause;
    private javax.swing.JMenuItem mPlay;
    private javax.swing.JMenuItem mPrevious;
    private javax.swing.JMenuItem mQuit;
    private javax.swing.JMenuItem mRemoveAll;
    private javax.swing.JMenuItem mRemoveSong;
    public static javax.swing.JRadioButtonMenuItem mRepeat;
    private javax.swing.JMenuItem mRepeatTrack;
    private javax.swing.JMenuItem mSavePlayList;
    private javax.swing.JMenuItem mShowClipboard;
    public static javax.swing.JRadioButtonMenuItem mShuffle;
    private javax.swing.JMenuItem mSongDown;
    private javax.swing.JMenuItem mSongUp;
    public static javax.swing.JMenuItem mSoundVolume;
    private javax.swing.JMenuItem mStop;
    private javax.swing.JMenuItem mid3info;
    private javax.swing.JMenuItem mid3info1;
    private javax.swing.JToolBar tbInfo;
    private javax.swing.JToolBar tbListEditor;
    private javax.swing.JToolBar tbNiz;
    private javax.swing.JToolBar tbOpen;
    private javax.swing.JToolBar tbOptions;
    private javax.swing.JToolBar tbTrackActions;
    private javax.swing.JToolBar tbVerh;
    // End of variables declaration//GEN-END:variables
}
