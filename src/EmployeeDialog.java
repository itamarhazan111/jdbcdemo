import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;


public class EmployeeDialog extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JTextField firstNameTextField;
    private JTextField lastNameTextField;
    private JTextField emailTextField;
    private JTextField salaryTextField;

    private EmployeeDAO employeeDAO;

    private EmployeeSearchApp employeeSearchApp;

    private Employee previousEmployee = null;
    private boolean updateMode = false;

    private int userId;

    public EmployeeDialog(EmployeeSearchApp theEmployeeSearchApp,
                          EmployeeDAO theEmployeeDAO, Employee thePreviousEmployee, boolean theUpdateMode, int theUserId) {
        this();
        employeeDAO = theEmployeeDAO;
        employeeSearchApp = theEmployeeSearchApp;

        previousEmployee = thePreviousEmployee;

        updateMode = theUpdateMode;

        userId = theUserId;

        if (updateMode) {
            setTitle("Update Employee");

            populateGui(previousEmployee);
        }
    }

    private void populateGui(Employee theEmployee) {

        firstNameTextField.setText(theEmployee.getFirstName());
        lastNameTextField.setText(theEmployee.getLastName());
        emailTextField.setText(theEmployee.getEmail());
        salaryTextField.setText(theEmployee.getSalary().toString());
    }

    public EmployeeDialog(EmployeeSearchApp theEmployeeSearchApp,
                          EmployeeDAO theEmployeeDAO, int theUserId) {
        this(theEmployeeSearchApp, theEmployeeDAO, null, false, theUserId);
    }

    /**
     * Create the dialog.
     */
    public EmployeeDialog() {
        setTitle("Add Employee");
        setBounds(100, 100, 450, 234);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel
                .setLayout(new FormLayout(new ColumnSpec[] {
                        FormFactory.RELATED_GAP_COLSPEC,
                        FormFactory.DEFAULT_COLSPEC,
                        FormFactory.RELATED_GAP_COLSPEC,
                        ColumnSpec.decode("default:grow"), }, new RowSpec[] {
                        FormFactory.RELATED_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.RELATED_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.RELATED_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.RELATED_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC, }));
        {
            JLabel lblFirstName = new JLabel("First Name");
            contentPanel.add(lblFirstName, "2, 2, right, default");
        }
        {
            firstNameTextField = new JTextField();
            contentPanel.add(firstNameTextField, "4, 2, fill, default");
            firstNameTextField.setColumns(10);
        }
        {
            JLabel lblLastName = new JLabel("Last Name");
            contentPanel.add(lblLastName, "2, 4, right, default");
        }
        {
            lastNameTextField = new JTextField();
            contentPanel.add(lastNameTextField, "4, 4, fill, default");
            lastNameTextField.setColumns(10);
        }
        {
            JLabel lblNewLabel = new JLabel("Email");
            contentPanel.add(lblNewLabel, "2, 6, right, default");
        }
        {
            emailTextField = new JTextField();
            contentPanel.add(emailTextField, "4, 6, fill, default");
            emailTextField.setColumns(10);
        }
        {
            JLabel lblSalary = new JLabel("Salary");
            contentPanel.add(lblSalary, "2, 8, right, default");
        }
        {
            salaryTextField = new JTextField();
            contentPanel.add(salaryTextField, "4, 8, fill, default");
            salaryTextField.setColumns(10);
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("Save");
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        saveEmployee();
                    }
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        setVisible(false);
                        dispose();
                    }
                });
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
    }

    protected BigDecimal convertStringToBigDecimal(String salaryStr) {

        BigDecimal result = null;

        try {
            double salaryDouble = Double.parseDouble(salaryStr);

            result = BigDecimal.valueOf(salaryDouble);
        } catch (Exception exc) {
            System.out.println("Invalid value. Defaulting to 0.0");
            result = BigDecimal.valueOf(0.0);
        }

        return result;
    }

    protected void saveEmployee() {

        // get the employee info from gui
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String email = emailTextField.getText();

        String salaryStr = salaryTextField.getText();
        BigDecimal salary = convertStringToBigDecimal(salaryStr);

        Employee tempEmployee = null;

        if (updateMode) {
            tempEmployee = previousEmployee;

            tempEmployee.setLastName(lastName);
            tempEmployee.setFirstName(firstName);
            tempEmployee.setEmail(email);
            tempEmployee.setSalary(salary);

        } else {
            tempEmployee = new Employee(lastName, firstName, email, salary);
        }

        try {
            // save to the database
            if (updateMode) {
                employeeDAO.updateEmployee(tempEmployee, userId);
            } else {
                employeeDAO.addEmployee(tempEmployee, userId);
            }

            // close dialog
            setVisible(false);
            dispose();

            // refresh gui list
            employeeSearchApp.refreshEmployeesView();

            // show success message
            JOptionPane.showMessageDialog(employeeSearchApp,
                    "Employee saved succesfully.", "Employee Saved",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception exc) {
            JOptionPane.showMessageDialog(employeeSearchApp,
                    "Error saving employee: " + exc.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

    }
}
