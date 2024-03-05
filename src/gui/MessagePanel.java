package gui;

import controller.MessageServer;
import model.Message;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

class ServerInfo {
    private String name;
    private int id;
    private boolean checked;

    public ServerInfo(String name, int id, boolean checked) {
        this.name = name;
        this.id = id;
        this.checked = checked;
    }

    public int getId() {
        return id;
    }

    public String toString() {
        return name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}

public class MessagePanel extends JPanel implements ProgressDialogListener {
    private JTree serverTree;
    private ServerTreeCellRenderer treeCellRenderer;
    private ServerTreeCellEditor treeCellEditor;
    private Set<Integer> selectedServers;
    private MessageServer messageServer;
    private ProgressDialog progressDialog;
    private SwingWorker<List<Message>, Integer> worker;
    private TextPanel textPanel;
    private JList messageList;
    private JSplitPane upperPane;
    private JSplitPane lowerPane;
    private DefaultListModel messageListModel;

    public MessagePanel(JFrame parent) {

        messageListModel = new DefaultListModel<>();

        progressDialog = new ProgressDialog(parent, "Messages Downloading...");

        messageServer = new MessageServer();

        progressDialog.setListener(this);

        selectedServers = new TreeSet<Integer>();
        selectedServers.add(0);
        selectedServers.add(1);
        selectedServers.add(4);

        treeCellRenderer = new ServerTreeCellRenderer();
        treeCellEditor = new ServerTreeCellEditor();

        serverTree = new JTree(createTree());
        serverTree.setCellRenderer(treeCellRenderer);
        serverTree.setCellEditor(treeCellEditor);
        serverTree.setEditable(true);

        serverTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

//        serverTree.addTreeSelectionListener(new TreeSelectionListener() {
//            @Override
//            public void valueChanged(TreeSelectionEvent e) {
//                DefaultMutableTreeNode node = (DefaultMutableTreeNode) serverTree.getLastSelectedPathComponent();
//
//                Object userObject = node.getUserObject();
//
//                System.out.println(userObject);
//            }
//        });

        messageServer.setSelectedServers(selectedServers);

        treeCellEditor.addCellEditorListener(new CellEditorListener() {
            @Override
            public void editingStopped(ChangeEvent e) {
                ServerInfo info = (ServerInfo) treeCellEditor.getCellEditorValue();

                //System.out.println(info + ": " + info.isChecked());

                int serverId = info.getId();

                if (info.isChecked()) {
                    selectedServers.add(serverId);
                } else {
                    selectedServers.remove(serverId);
                }

                messageServer.setSelectedServers(selectedServers);


                retrieveMessage();
            }

            @Override
            public void editingCanceled(ChangeEvent e) {

            }
        });

        setLayout(new BorderLayout());

        textPanel = new TextPanel();
        messageList = new JList<>(messageListModel);
        messageList.setCellRenderer(new MessageListRenderer());

        messageList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                Message message = (Message) messageList.getSelectedValue();

                textPanel.setText(message.getContents());
            }
        });

        lowerPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(messageList), textPanel);
        upperPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(serverTree), lowerPane);

        textPanel.setMinimumSize(new Dimension(10, 100));
        messageList.setMinimumSize(new Dimension(10, 100));

        upperPane.setResizeWeight(0.5);
        lowerPane.setResizeWeight(0.5);

        add(upperPane, BorderLayout.CENTER);
    }

    public void refresh() {
        retrieveMessage();
    }

    private void retrieveMessage() {

        //System.out.println("Messages waiting: " + messageServer.getMessageCount());

        progressDialog.setMaximum(messageServer.getMessageCount());

       /* SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {*/
        progressDialog.setVisible(true);
        /*    }
        });
*/
        worker = new SwingWorker<List<Message>, Integer>() {
            @Override
            protected List<Message> doInBackground() throws Exception {

                List<Message> retrievedMessages = new CopyOnWriteArrayList<>(); //new ArrayList<>();

                int count = 0;

                for (Message message : messageServer) {

                    if (isCancelled()) break;

                    System.out.println(message.getTitle());

                    retrievedMessages.add(message);

                    count++;

                    publish(count);
                }
                return retrievedMessages;
            }

            @Override
            protected void done() {

                progressDialog.setVisible(false);

                if (isCancelled()) return;

                try {
                    List<Message> retrieveMessages = get();
                    //System.out.println("Retrieved " + retrieveMessages.size() + " messages.");
                    messageListModel.removeAllElements();

                    for (Message message : retrieveMessages) {
                        messageListModel.addElement(message);
                    }

                    messageList.setSelectedIndex(0);

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected void process(List<Integer> counts) {
                int retrieved = counts.get(counts.size() - 1);
                //System.out.println("Got " + retrieved + " messages.");
                progressDialog.setValue(retrieved);
            }
        };

        worker.execute();

    }

    private DefaultMutableTreeNode createTree() {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("Servers");

        DefaultMutableTreeNode branch1 = new DefaultMutableTreeNode("USA");

        DefaultMutableTreeNode server1 = new DefaultMutableTreeNode(new ServerInfo("New York", 0, selectedServers.contains(0)));
        DefaultMutableTreeNode server2 = new DefaultMutableTreeNode(new ServerInfo("Boston", 1, selectedServers.contains(1)));
        DefaultMutableTreeNode server3 = new DefaultMutableTreeNode(new ServerInfo("Los Angeles", 2, selectedServers.contains(2)));

        branch1.add(server1);
        branch1.add(server2);
        branch1.add(server3);

        DefaultMutableTreeNode branch2 = new DefaultMutableTreeNode("UK");
        DefaultMutableTreeNode server4 = new DefaultMutableTreeNode(new ServerInfo("London", 3, selectedServers.contains(3)));
        DefaultMutableTreeNode server5 = new DefaultMutableTreeNode(new ServerInfo("Edinburgh", 4, selectedServers.contains(4)));

        branch2.add(server4);
        branch2.add(server5);

        top.add(branch1);
        top.add(branch2);

        return top;

    }

    @Override
    public void progressDialogCancelled() {
        if (worker != null) {
            worker.cancel(true);
        }
    }
}