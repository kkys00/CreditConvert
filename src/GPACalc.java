import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

class PlaceholderTextField extends JTextField {
    private String placeholder;

    public PlaceholderTextField(String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getText().isEmpty()) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.GRAY);
            g2.drawString(placeholder, getInsets().left, getHeight() / 2 + g.getFontMetrics().getAscent() / 2 - 2);
        }
    }
}

public class GPACalc extends JFrame {
	static HashMap<String, Double> alphabetTogpa43;
	static String[] header43 = {"등급", "A+", "A", "A-",
			"B+", "B", "B-", "C+", "C", "C-",
			"D+", "D", "D-", "F"}; //헤더는 1차원 배열
	static String[][] contents43 = {{"성적점", "4.3", "4.0", "3.7",
		"3.3", "3.0", "2.7", "2.3", "2.0", "1.7",
		"1.3", "1.0", "0.7", "0.0"}}; //내용은 2차원 배열
	static String[] header45and100 = {"4.3점 만점", "4.5점 만점", "백분율"};
	static String[][] contents45and100 =  new String[431][3];
	JButton addGrade, getResult;
	int selectedRow = -1;
	
	GPACalc(){
		setTitle("학점 계산기");
		Create43Dict();
		create45And100();
		
		//1. 왼쪽/오른쪽 패널 생성
		JPanel infoPane = new JPanel();
		JPanel inputPane = new JPanel();
		
		////////////////////////////////////////////////////////////////////
		//<1> 왼쪽 패널 (1) title/footer
		JLabel criteria = new JLabel("계산/환산 기준표");
		JLabel source = new JLabel("출처: 이화여자대학교 | 학사안내 | 학사정보 | 성적 | 성적 | 성적 평가 표시 방법, 평균평점 환산");
		
		//<1> 왼쪽 패널 (2) 4.3점 만점 기준표
		JPanel criteriaPane = new JPanel();
		JPanel pane43 = new JPanel();
		JLabel lbl43 = new JLabel("4.3점 성적 기준");
		JTable table43 = new JTable(contents43, header43);
        JScrollPane scrollPanel43 = new JScrollPane(table43);
        scrollPanel43.setPreferredSize(new Dimension(200, 18));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < table43.getColumnCount(); i++) {
            table43.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        pane43.setLayout(new BoxLayout(pane43, BoxLayout.Y_AXIS));
        pane43.add(lbl43);
        pane43.add(scrollPanel43);
       
        //<1> 왼쪽 패널 (3) 4.5점 만점 / 백분율 환산표 
        JPanel pane45and100 = new JPanel();
		JLabel lbl45and100 = new JLabel("4.3 -> 4.5/백분율 환산표");
		JTable table45and100 = new JTable(contents45and100, header45and100);
		JScrollPane scrollPanel45and100 = new JScrollPane(table45and100);
		
		pane45and100.add(lbl45and100);
		pane45and100.add(scrollPanel45and100);
		
		pane43.setBackground(Color.CYAN);
		pane45and100.setBackground(Color.YELLOW);
		
		////////////////
		criteriaPane.setLayout(new BoxLayout(criteriaPane, BoxLayout.Y_AXIS));
		criteriaPane.add(pane43);
		criteriaPane.add(pane45and100);
		
		////////////////
		infoPane.setBackground(Color.LIGHT_GRAY);
		infoPane.setLayout(new BorderLayout());
		infoPane.add(criteria,"North");
		infoPane.add(criteriaPane, "Center");
		infoPane.add(source, "South");
		
		////////////////////////////////////////////////////////////////////
		//<2> 오른쪽 패널 (1) title/footer
		JLabel gradeIn = new JLabel("성적 정보 입력");
		JLabel author = new JLabel("2024-2학년도 JAVA프로그래밍및실습(04) 김영서 기말과제");
		
		JPanel paneGradeAndResult = new JPanel();
		
		//<2> 오른쪽 패널 (2) 직접 입력한 정보를 표시할 테이블
		String[] userGradesInfoHeader = {"구분", "학점", "성적"};
		DefaultTableModel tableModel = new DefaultTableModel(userGradesInfoHeader, 0); // 초기 행 0개
        JTable userGradesInfoTable = new JTable(tableModel);
		JScrollPane scrollGrade = new JScrollPane(userGradesInfoTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED ,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollGrade.setPreferredSize(new Dimension(550, 300));
		
		userGradesInfoTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
            	selectedRow = userGradesInfoTable.getSelectedRow();
            	System.out.println(selectedRow + "행 선택됨");
            }
        });
		
		//<2> 오른쪽 패널 (2) 정보를 입력 받을 텍스트 필드 및 콤보 박스
		JPanel inputContainer = new JPanel(new GridLayout(1, 4, 5, 5));
		JTextField tfCourseNumber = new PlaceholderTextField("학수번호/과목명");
		JTextField tfCredits = new PlaceholderTextField("학점 1, 2, 3, ...");
        String[] gradeArray = Arrays.copyOfRange(header43, 1, header43.length);
        JComboBox tfGrade = new JComboBox(gradeArray);
		addGrade = new JButton("성적 추가하기");
		addGrade.addActionListener(new ActionListener() {
			
			void addGrades() {
				String course = tfCourseNumber.getText();
				Integer credit = Integer.parseInt(tfCredits.getText());
				String grade = (String) tfGrade.getSelectedItem();
				tableModel.addRow(new Object[]{course, credit, grade});
				System.out.println(course + ", " + credit + ", " + grade + ", " + alphabetTogpa43.get(grade));
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean isCourseNum = tfCourseNumber.getText().isEmpty();
				boolean isCredits = tfCredits.getText().isEmpty();
				
				if(isCourseNum) JOptionPane.showMessageDialog(null, "학수번호를 입력해주세요", 
                		"Message", JOptionPane.INFORMATION_MESSAGE);
				if(isCredits) JOptionPane.showMessageDialog(null, "학점을 입력해주세요", 
                		"Message", JOptionPane.INFORMATION_MESSAGE);
				
				if(!isCourseNum && !isCredits) {
					addGrades();
					tfCourseNumber.setText("");
				}
				
			}
		});
		
		inputContainer.add(tfCourseNumber);
		inputContainer.add(tfCredits);
		inputContainer.add(tfGrade);
		inputContainer.add(addGrade);
		
		paneGradeAndResult.add(inputContainer, "North");
		paneGradeAndResult.add(scrollGrade, "Center");
		
		//<2> 오른쪽 패널 (3) 환산된 결과를 표시할 테이블
		String[][] contentsResult = {{"","",""}};
		JTable tableResult = new JTable(contentsResult, header45and100);
		tableResult.setRowHeight(25);
		for (int i = 0; i < tableResult.getColumnCount(); i++) {
			tableResult.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        JScrollPane scrollPaneResult = new JScrollPane(tableResult, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER ,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPaneResult.setPreferredSize(new Dimension(500, 50));
		
        //<2> 오른쪽 패널 (4) csv 파일 선택 버튼, 성적 계산 버튼, 성적 삭제 버튼
        JPanel btnPane = new JPanel();
        btnPane.setLayout(new BorderLayout(30, 10));
        
		getResult = new JButton("4.3/4.5/백분율 성적 계산하기");
		getResult.addActionListener(new ActionListener() {
			void clearField() {
				tfCourseNumber.setText("");
				tfCredits.setText("");
				tfGrade.setSelectedIndex(0);
			}
			@Override
			public void actionPerformed(ActionEvent e) {
				int totalCredit = 0;
				double total43Sum = 0.0;
				DefaultTableModel model = (DefaultTableModel) userGradesInfoTable.getModel();

				for (int row = 0; row < model.getRowCount(); row++) {
					Integer credit = (Integer) model.getValueAt(row, 1);
					String alphabetGrade = (String) model.getValueAt(row, 2);
					totalCredit += credit;
					total43Sum += alphabetTogpa43.get(alphabetGrade) * credit;
				}
				
				double result43 = doubleformat2(total43Sum/totalCredit);
				System.out.println("총 학점: " + totalCredit 
						+ "// 4.3 만점 기준 " + total43Sum 
						+ "// 평균 평점: " + doubleformat2(total43Sum/totalCredit)
						+ "//45만점: " + Calc45(result43)
						+ "//백분율: " + Calc100(result43));

				tableResult.setValueAt(Double.toString(result43), 0, 0);
				tableResult.setValueAt(Double.toString(Calc45(result43)), 0, 1);
				tableResult.setValueAt(Double.toString(Calc100(result43)), 0, 2);
				
				clearField();
			}
		});
		
		JButton deleteRow = new JButton("성적 삭제하기");
		deleteRow.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectedRow != -1) { // 유효한 행이 선택된 경우
                    int confirm = JOptionPane.showConfirmDialog(null, "정말 삭제하시겠습니까?", "행 삭제", 
                    		JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        tableModel.removeRow(selectedRow);
                    }
                }
			}
		});
		
		JButton putFile = new JButton(".csv 파일 선택");
		putFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
	            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

	            // .csv 파일 필터 추가
	            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
	                @Override
	                public boolean accept(File f) {
	                    return f.isDirectory() || f.getName().toLowerCase().endsWith(".csv");
	                }

	                @Override
	                public String getDescription() {
	                    return "CSV 파일 (*.csv)";
	                }
	            });

	            int returnValue = fileChooser.showOpenDialog(null);
	            if (returnValue == JFileChooser.APPROVE_OPTION) {
	                File selectedFile = fileChooser.getSelectedFile();
	                try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
	                    String line;
	                    while ((line = reader.readLine()) != null) {
	                        String[] row = line.split(",");
	        				String course = row[0];
	        				Integer credit = Integer.parseInt(row[1]);
	        				String grade = row[2];
	                        tableModel.addRow(new Object[]{course, credit, grade});
	                    }
	                } catch (IOException ex) {
	                    JOptionPane.showMessageDialog(null, "파일 읽기 실패: " + ex.getMessage(),
	                            "Error", JOptionPane.ERROR_MESSAGE);
	                }
	            }
				
			}
		});
		
		btnPane.add(putFile, "West");
		btnPane.add(getResult, "Center");
		btnPane.add(deleteRow, "East");
		paneGradeAndResult.add(btnPane, "South");
		paneGradeAndResult.add(scrollPaneResult);
		
		//<2> 오른쪽 패널 (5) csv 파일 선택 버튼, 성적 계산 버튼, 성적 삭제 버튼에 대한 가이드
		JPanel paneResult = new JPanel();
		JLabel howToEnterFile = new JLabel("1. .csv 파일 선택 버튼을 클릭하여 성적을 입력할 수 있습니다.");
		JLabel howToEdit = new JLabel("2. 더블 클릭하여 값을 수정할 수 있습니다.");
		JLabel howToDelete = new JLabel("3. 행을 선택하고 성적 삭제하기 버튼을 클릭하여 값을 지울 수 있습니다.");
		paneResult.setLayout(new BoxLayout(paneResult, BoxLayout.Y_AXIS));
		paneResult.setBackground(Color.LIGHT_GRAY);
		paneResult.add(howToEnterFile);
		paneResult.add(howToEdit);
		paneResult.add(howToDelete);
		paneGradeAndResult.add(paneResult);
		
		inputPane.setBackground(Color.LIGHT_GRAY);
		inputPane.setLayout(new BorderLayout());
		inputPane.add(gradeIn, "North");
		inputPane.add(paneGradeAndResult, "Center");
		inputPane.add(author, "South");
		
		////////////////////////////////////////////////////////////////////
		//<3> 전체 GUI 설정
		setLayout(new GridLayout(1, 2));
		add(infoPane);
		add(inputPane);
		setSize(1100, 600);
		setVisible(true);
	}
	
	double doubleformat2(double val) {
		return Math.round(val * 100) / 100.0;
	}
	
	void Create43Dict() {
		alphabetTogpa43 = new HashMap<>();
		
		for(int i=1; i<header43.length; i++) {
			alphabetTogpa43.put(header43[i], Double.parseDouble(contents43[0][i]));
		}
	}
	
	double Calc45(double avg) {
		double gpa = 0;
		
		if(avg > 4.27) gpa = avg + 0.20;	
		else if(avg <= 4.27 && avg >= 3.74) {
			double bound = 4.27;
			double calc = bound - avg;
			double round = Math.round(calc * 100)/6 + 1;
			gpa = avg + 0.20 + round/100;
		}
		else if(avg >= 0.01 && avg < 3.74) gpa = avg + 0.30;
		else if(avg < 0.01) gpa = avg;
		
		return doubleformat2(gpa);
	}
	
	double Calc100(double avg) {
		double gpa = 0;
		if(avg >= 4.00) gpa = 72 + ((avg - 0.7) * 28 / 3.6);
		else if(avg < 4.00 && avg >= 3.70) gpa = 69 + ((avg - 0.7) * 31 / 3.6);
		else if(avg < 3.70 && avg >= 3.30) gpa = 66 + ((avg - 0.7) * 34 / 3.6);
		else if(avg < 3.30 && avg >= 3.00) gpa = 65 + ((avg - 0.7) * 35 / 3.6);
		else if(avg < 3.00 && avg >= 2.70) gpa = 64 + ((avg - 0.7) * 36 / 3.6);
		else if(avg <2.70) gpa = 63 + ((avg - 0.7) * 37 / 3.6);
		
		return Math.round(gpa* 10) / 10.0;
	}
	
	void create45And100() {
		double st = 4.27;
		double grade = 4.30;
		for(int i=0; i<431; i++){
			contents45and100[i][0] = String.format("%.2f", grade);
			contents45and100[i][1] = String.format("%.2f", Calc45(grade));
			contents45and100[i][2] = Double.toString(Calc100(grade));
			
			grade = Math.round((grade - 0.01) * 100) / 100.0;
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new GPACalc();
		
	}

}
