package net.jurvur.modules;

import com.avaje.ebean.Ebean;
import net.jurvur.Module;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.hooks.events.MessageEvent;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

public class NoteModule extends Module {
    public NoteModule() {
        addCommand("note", "makeNote");
        addPattern(".*", "showNoteMaybe");

        addHelpCommand("note", "Sends a user a note, which they will receive upon next message", "!note Kieraan you suck", "!note IAmCloud Congratulations!");
    }

    public void makeNote(MessageEvent e, List<String> args) {
        if (args.size() > 1) {
            String recipient = args.get(0);
            String noteContent = StringUtils.join(args.subList(1, args.size()), " ");

            Note note = new Note();
            note.setChannel(e.getChannel().getName());
            note.setContent(noteContent);
            note.setRead(false);
            note.setRecipient(recipient);
            note.setSender(e.getUser().getNick());


            try {
                Ebean.save(note);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.respond("Note saved.");
        }
    }

    public void showNoteMaybe(MessageEvent e, List<String> args) {
        List<Note> ns = Ebean.find(Note.class).where().eq("recipient", e.getUser().getNick()).eq("read", false).eq("channel", e.getChannel().getName()).findList();

        if (ns.size() > 0) {
            e.respond("You have notes: ");
            for (Note n : ns) {
                e.respond("<" + n.getSender() + "> " + n.getContent() + " ");
                n.setRead(true);
                Ebean.save(n);
            }
        }
    }

    @Entity
    @Table(name="note")
    public static class Note {
        @Id
        private Long id;
        private String sender = "";
        private String recipient = "";
        private String content = "";
        private String channel = "";
        private Boolean read = false;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public String getRecipient() {
            return recipient;
        }

        public void setRecipient(String recipient) {
            this.recipient = recipient;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public Boolean getRead() {
            return read;
        }

        public void setRead(Boolean read) {
            this.read = read;
        }

    }
}
