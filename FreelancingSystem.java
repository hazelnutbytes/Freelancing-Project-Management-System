
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;

class Freelancer {

    int id;
    String name, email;
    String[] skills;

    Freelancer(int id, String name, String email, String[] skills) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.skills = skills;
    }

    public String toString() {
        return id + " | " + name + " | " + email + " | "
                + String.join(", ", skills);
    }
}

class Client {

    int id;
    String name, email;

    Client(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String toString() {
        return id + " | " + name + " | " + email;
    }
}

class Project {

    int id;
    String title, deadline, status;
    double budget;
    String[] requirements;
    Client client;
    Freelancer freelancer;

    Project(int id, String title, double budget, String deadline,
            String[] requirements, Client client) {

        if (budget <= 0) {
            throw new IllegalArgumentException("Invalid budget");
        }

        if (!validDate(deadline)) {
            throw new IllegalArgumentException("Invalid deadline");
        }

        this.id = id;
        this.title = title;
        this.budget = budget;
        this.deadline = deadline;
        this.requirements = requirements;
        this.client = client;
        status = "Open";
    }

    static boolean validDate(String s) {
        try {
            SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
            f.setLenient(false);
            f.parse(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String toString() {
        return id + " | " + title + " | ₹" + budget + " | "
                + deadline + " | " + status + " | " + client.name;
    }
}

class Proposal {

    int id;
    Freelancer freelancer;
    Project project;
    double amount;
    String status;

    Proposal(int id, Freelancer freelancer, Project project, double amount) {

        if (amount <= 0 || amount > project.budget) {
            throw new IllegalArgumentException("Invalid proposal amount");
        }

        this.id = id;
        this.freelancer = freelancer;
        this.project = project;
        this.amount = amount;
        status = "Pending";
    }

    public String toString() {
        return id + " | " + freelancer.name + " | "
                + project.title + " | ₹" + amount + " | " + status;
    }
}

class Payment {

    int id;
    Project project;
    double amount;
    String status;

    Payment(int id, Project project, double amount) {

        if (amount <= 0 || amount > project.budget) {
            throw new IllegalArgumentException("Invalid payment amount");
        }

        this.id = id;
        this.project = project;
        this.amount = amount;
        status = "Pending";
    }

    public String toString() {
        return id + " | " + project.title + " | ₹"
                + amount + " | " + status;
    }
}

public class FreelancingSystem extends JFrame implements ActionListener {

    ArrayList<Freelancer> freelancers = new ArrayList<>();
    ArrayList<Client> clients = new ArrayList<>();
    ArrayList<Project> projects = new ArrayList<>();
    ArrayList<Payment> payments = new ArrayList<>();

    LinkedList<Proposal> proposals = new LinkedList<>();
    LinkedList<String> history = new LinkedList<>();

    HashMap<Integer, Project> projectMap = new HashMap<>();

    TreeMap<Double, ArrayList<Project>> budgetMap = new TreeMap<>();
    TreeMap<String, Project> deadlineMap = new TreeMap<>();

    JTextField id = new JTextField();
    JTextField name = new JTextField();
    JTextField email = new JTextField();
    JTextField project = new JTextField();
    JTextField budget = new JTextField();
    JTextField deadline = new JTextField();
    JTextField skills = new JTextField();
    JTextField search = new JTextField();
    JTextField proposalId = new JTextField();
    JTextField proposalAmount = new JTextField();
    JTextField paymentId = new JTextField();
    JTextField paymentAmount = new JTextField();

    JTextArea output = new JTextArea();

    FreelancingSystem() {

        setTitle("Freelancing Project Management System");
        setSize(1050, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel title = new JLabel(
                "Freelancing Project Management System",
                SwingConstants.CENTER
        );
        title.setBounds(250, 20, 500, 30);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title);

        addField("ID:", id, 70);
        addField("Name:", name, 110);
        addField("Email:", email, 150);
        addField("Project:", project, 190);
        addField("Budget:", budget, 230);
        addField("Deadline:", deadline, 270);
        addField("Skills:", skills, 310);
        addField("Search:", search, 350);
        addField("Proposal ID:", proposalId, 390);
        addField("Proposal Amt:", proposalAmount, 430);
        addField("Payment ID:", paymentId, 470);
        addField("Payment Amt:", paymentAmount, 510);

        String[] names = {
            "Add Freelancer", "Update Freelancer",
            "Delete Freelancer", "View Freelancers",
            "Search Freelancer", "Add Client",
            "Update Client", "Delete Client",
            "View Clients", "Add Project",
            "Update Project", "Delete Project",
            "View Projects", "Search Project",
            "Sort Budget", "Sort Deadline",
            "Update Status", "Add Proposal",
            "View Proposals", "Accept Proposal",
            "Reject Proposal", "Shortlist Freelancer",
            "Add Payment", "View Payments",
            "Complete Payment", "Project Report"
        };

        int x = 350;
        int y = 70;

        for (int i = 0; i < names.length; i++) {

            JButton b = new JButton(names[i]);

            int col = i % 3;
            int row = i / 3;

            b.setBounds(
                    x + col * 205,
                    y + row * 42,
                    190,
                    32
            );

            b.setActionCommand(names[i]);
            b.addActionListener(this);
            add(b);
        }

        output.setEditable(false);
        output.setFont(new Font("Monospaced", Font.PLAIN, 13));

        JScrollPane scroll = new JScrollPane(output);
        scroll.setBounds(350, 440, 600, 210);

        add(scroll);

        setVisible(true);
    }

    void addField(String text, JTextField field, int y) {

        JLabel label = new JLabel(text);
        label.setBounds(30, y, 100, 25);
        add(label);

        field.setBounds(135, y, 180, 25);
        add(field);
    }

    Freelancer getFreelancer(int n) {

        for (Freelancer f : freelancers) {
            if (f.id == n) {
                return f;
            }
        }

        return null;
    }

    Client getClient(int n) {

        for (Client c : clients) {
            if (c.id == n) {
                return c;
            }
        }

        return null;
    }

    Project getProject(int n) {
        return projectMap.get(n);
    }

    Proposal getProposal(int n) {

        for (Proposal p : proposals) {
            if (p.id == n) {
                return p;
            }
        }

        return null;
    }

    Payment getPayment(int n) {

        for (Payment p : payments) {
            if (p.id == n) {
                return p;
            }
        }

        return null;
    }

    int num(JTextField f) {
        return Integer.parseInt(f.getText().trim());
    }

    double money(JTextField f) {
        return Double.parseDouble(f.getText().trim());
    }

    String[] array(String s) {
        return s.trim().split("\\s*,\\s*");
    }

    boolean validEmail(String s) {
        return s.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }

    void msg(String s) {
        JOptionPane.showMessageDialog(this, s);
    }

    void show(String s) {
        output.setText(s);
    }

    void addFreelancer() {

        try {
            int n = num(id);

            if (getFreelancer(n) != null) {
                throw new Exception("Freelancer ID already exists");
            }

            if (name.getText().isEmpty()
                    || !validEmail(email.getText())
                    || skills.getText().isEmpty()) {
                throw new Exception("Enter valid freelancer details");
            }

            freelancers.add(new Freelancer(
                    n,
                    name.getText(),
                    email.getText(),
                    array(skills.getText())
            ));

            msg("Freelancer added");

        } catch (NumberFormatException e) {
            msg("Freelancer ID must be numeric");
        } catch (Exception e) {
            msg(e.getMessage());
        }
    }

    void updateFreelancer() {

        try {
            Freelancer f = getFreelancer(num(id));

            if (f == null) {
                throw new Exception("Freelancer not found");
            }

            f.name = name.getText();
            f.email = email.getText();
            f.skills = array(skills.getText());

            msg("Freelancer updated");

        } catch (Exception e) {
            msg("Enter valid freelancer details");
        }
    }

    void deleteFreelancer() {

        try {
            Freelancer f = getFreelancer(num(id));

            if (f == null) {
                throw new Exception();
            }

            freelancers.remove(f);
            msg("Freelancer deleted");

        } catch (Exception e) {
            msg("Enter valid Freelancer ID");
        }
    }

    void viewFreelancers() {

        String s = "FREELANCERS\n\n";

        for (Freelancer f : freelancers) {
            s += f + "\n";
        }

        show(s);
    }

    void searchFreelancer() {

        String q = search.getText().toLowerCase();
        String s = "SEARCH RESULTS\n\n";

        for (Freelancer f : freelancers) {

            for (String skill : f.skills) {

                if (skill.toLowerCase().contains(q)) {
                    s += f + "\n";
                    break;
                }
            }
        }

        show(s);
    }

    void addClient() {

        try {
            int n = num(id);

            if (getClient(n) != null) {
                throw new Exception("Client ID already exists");
            }

            if (name.getText().isEmpty()
                    || !validEmail(email.getText())) {
                throw new Exception("Enter valid client details");
            }

            clients.add(new Client(
                    n,
                    name.getText(),
                    email.getText()
            ));

            msg("Client added");

        } catch (NumberFormatException e) {
            msg("Client ID must be numeric");
        } catch (Exception e) {
            msg(e.getMessage());
        }
    }

    void updateClient() {

        try {
            Client c = getClient(num(id));

            if (c == null) {
                throw new Exception();
            }

            c.name = name.getText();
            c.email = email.getText();

            msg("Client updated");

        } catch (Exception e) {
            msg("Enter valid client details");
        }
    }

    void deleteClient() {

        try {
            Client c = getClient(num(id));

            if (c == null) {
                throw new Exception();
            }

            clients.remove(c);
            msg("Client deleted");

        } catch (Exception e) {
            msg("Enter valid Client ID");
        }
    }

    void viewClients() {

        String s = "CLIENTS\n\n";

        for (Client c : clients) {
            s += c + "\n";
        }

        show(s);
    }

    void addProject() {

        try {
            int n = num(id);
            Client c = getClient(num(search));

            if (getProject(n) != null) {
                throw new Exception("Project ID already exists");
            }

            if (c == null) {
                throw new Exception("Client not found");
            }

            Project p = new Project(
                    n,
                    project.getText(),
                    money(budget),
                    deadline.getText(),
                    array(skills.getText()),
                    c
            );

            projects.add(p);
            projectMap.put(n, p);

            budgetMap
                    .computeIfAbsent(p.budget,
                            k -> new ArrayList<>())
                    .add(p);

            deadlineMap.put(
                    p.deadline + "-" + p.id,
                    p
            );

            history.add("Project added: " + p.title);

            msg("Project added");

        } catch (NumberFormatException e) {
            msg("Project ID, Client ID and Budget must be numeric");
        } catch (Exception e) {
            msg(e.getMessage());
        }
    }

    void updateProject() {

        try {
            Project p = getProject(num(id));

            if (p == null) {
                throw new Exception("Project not found");
            }

            p.title = project.getText();
            p.budget = money(budget);
            p.deadline = deadline.getText();
            p.requirements = array(skills.getText());

            msg("Project updated");

        } catch (Exception e) {
            msg("Enter valid project details");
        }
    }

    void deleteProject() {

        try {
            Project p = getProject(num(id));

            if (p == null) {
                throw new Exception();
            }

            projects.remove(p);
            projectMap.remove(p.id);

            msg("Project deleted");

        } catch (Exception e) {
            msg("Enter valid Project ID");
        }
    }

    void viewProjects() {

        String s = "PROJECTS\n\n";

        for (Project p : projects) {
            s += p + "\n";
        }

        show(s);
    }

    void searchProject() {

        String q = search.getText().toLowerCase();
        String s = "PROJECT SEARCH\n\n";

        for (Project p : projects) {

            for (String r : p.requirements) {

                if (r.toLowerCase().contains(q)) {
                    s += p + "\n";
                    break;
                }
            }
        }

        show(s);
    }

    void sortBudget() {

        String s = "PROJECTS SORTED BY BUDGET\n\n";

        for (ArrayList<Project> list : budgetMap.values()) {
            for (Project p : list) {
                s += p + "\n";
            }
        }

        show(s);
    }

    void sortDeadline() {

        String s = "PROJECTS SORTED BY DEADLINE\n\n";

        for (Project p : deadlineMap.values()) {
            s += p + "\n";
        }

        show(s);
    }

    void updateStatus() {

        try {
            Project p = getProject(num(id));

            if (p == null) {
                throw new Exception();
            }

            String status = JOptionPane.showInputDialog(
                    this,
                    "Enter Open, In Progress or Completed:"
            );

            if (status == null) {
                return;
            }

            p.status = status;

            history.add(
                    p.title + " status changed to " + status
            );

            msg("Status updated");

        } catch (Exception e) {
            msg("Enter valid Project ID");
        }
    }

    void addProposal() {

        try {
            int n = num(proposalId);

            Freelancer f = getFreelancer(num(id));
            Project p = getProject(num(project));

            if (f == null || p == null) {
                throw new Exception(
                        "Freelancer or Project not found"
                );
            }

            proposals.add(
                    new Proposal(
                            n,
                            f,
                            p,
                            money(proposalAmount)
                    )
            );

            msg("Proposal added");

        } catch (NumberFormatException e) {
            msg("IDs and Proposal Amount must be numeric");
        } catch (Exception e) {
            msg(e.getMessage());
        }
    }

    void viewProposals() {

        String s = "PROPOSALS\n\n";

        for (Proposal p : proposals) {
            s += p + "\n";
        }

        show(s);
    }

    void proposalAction(String status) {

        try {
            Proposal p = getProposal(num(proposalId));

            if (p == null) {
                throw new Exception();
            }

            p.status = status;

            if (status.equals("Accepted")) {
                p.project.freelancer = p.freelancer;
                p.project.status = "In Progress";
            }

            msg("Proposal " + status);

        } catch (Exception e) {
            msg("Enter valid Proposal ID");
        }
    }

    void shortlist() {

        try {
            Freelancer f = getFreelancer(num(id));
            Project p = getProject(num(project));

            if (f == null || p == null) {
                throw new Exception();
            }

            p.freelancer = f;

            msg("Freelancer shortlisted");

        } catch (Exception e) {
            msg("Enter valid Freelancer and Project IDs");
        }
    }

    void addPayment() {

        try {
            int n = num(paymentId);
            Project p = getProject(num(project));

            if (p == null) {
                throw new Exception("Project not found");
            }

            payments.add(
                    new Payment(
                            n,
                            p,
                            money(paymentAmount)
                    )
            );

            msg("Payment added");

        } catch (NumberFormatException e) {
            msg("IDs and Payment Amount must be numeric");
        } catch (Exception e) {
            msg(e.getMessage());
        }
    }

    void viewPayments() {

        String s = "PAYMENTS\n\n";

        for (Payment p : payments) {
            s += p + "\n";
        }

        show(s);
    }

    void completePayment() {

        try {
            Payment p = getPayment(num(paymentId));

            if (p == null) {
                throw new Exception();
            }

            p.status = "Completed";

            msg("Payment completed");

        } catch (Exception e) {
            msg("Enter valid Payment ID");
        }
    }

    void report() {
        try {
            int n = num(id);
            Project p = getProject(n);

            if (p == null) {
                msg("Project not found");
                return;
            }

            String s
                    = "PROJECT REPORT\n\n"
                    + "ID: " + p.id + "\n"
                    + "Title: " + p.title + "\n"
                    + "Budget: ₹" + p.budget + "\n"
                    + "Deadline: " + p.deadline + "\n"
                    + "Status: " + p.status + "\n"
                    + "Client: " + p.client.name + "\n"
                    + "Requirements: "
                    + String.join(", ", p.requirements) + "\n"
                    + "Freelancer: "
                    + (p.freelancer == null ? "Not selected" : p.freelancer.name)
                    + "\n\nPROPOSALS\n";

            for (Proposal x : proposals) {
                if (x.project == p) {
                    s += x + "\n";
                }
            }

            s += "\nPAYMENTS\n";

            for (Payment x : payments) {
                if (x.project == p) {
                    s += x + "\n";
                }
            }

            show(s);

        } catch (NumberFormatException e) {
            msg("Project ID must be numeric");
        } catch (Exception e) {
            msg("Error generating report: " + e.getMessage());
        }
    }

    public void actionPerformed(ActionEvent e) {

        switch (e.getActionCommand()) {

            case "Add Freelancer":
                addFreelancer();
                break;

            case "Update Freelancer":
                updateFreelancer();
                break;

            case "Delete Freelancer":
                deleteFreelancer();
                break;

            case "View Freelancers":
                viewFreelancers();
                break;

            case "Search Freelancer":
                searchFreelancer();
                break;

            case "Add Client":
                addClient();
                break;

            case "Update Client":
                updateClient();
                break;

            case "Delete Client":
                deleteClient();
                break;

            case "View Clients":
                viewClients();
                break;

            case "Add Project":
                addProject();
                break;

            case "Update Project":
                updateProject();
                break;

            case "Delete Project":
                deleteProject();
                break;

            case "View Projects":
                viewProjects();
                break;

            case "Search Project":
                searchProject();
                break;

            case "Sort Budget":
                sortBudget();
                break;

            case "Sort Deadline":
                sortDeadline();
                break;

            case "Update Status":
                updateStatus();
                break;

            case "Add Proposal":
                addProposal();
                break;

            case "View Proposals":
                viewProposals();
                break;

            case "Accept Proposal":
                proposalAction("Accepted");
                break;

            case "Reject Proposal":
                proposalAction("Rejected");
                break;

            case "Shortlist Freelancer":
                shortlist();
                break;

            case "Add Payment":
                addPayment();
                break;

            case "View Payments":
                viewPayments();
                break;

            case "Complete Payment":
                completePayment();
                break;

            case "Project Report":
                report();
                break;
        }
    }

    public static void main(String[] args) {
        new FreelancingSystem();
    }
}
