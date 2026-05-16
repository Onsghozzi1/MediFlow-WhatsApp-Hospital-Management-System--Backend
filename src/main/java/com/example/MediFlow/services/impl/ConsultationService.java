package com.example.MediFlow.services.impl;

import com.example.MediFlow.Dtos.consultation.*;
import com.example.MediFlow.entity.*;
import com.example.MediFlow.entity.enums.ConsultationStatus;
import com.example.MediFlow.entity.enums.Status;
import com.example.MediFlow.exception.DoctorException;
import com.example.MediFlow.exception.UserServiceCustomException;
import com.example.MediFlow.mapper.ConsultationMapper;
import com.example.MediFlow.repository.*;
import com.example.MediFlow.repository.query.IConsultationQuery;
import com.example.MediFlow.services.IConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static java.lang.Integer.parseInt;

@Service
public class ConsultationService implements IConsultationService {

    @Autowired
    private ConsultationServiceRepository consultationServiceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private IConsultationQuery iConsultationQuery;
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private ConsultationMapper consultationMapper;
    @Autowired
    private PrescriptionRepository prescriptionRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserServiceCustomException("User not found","error"));
    }


//    public void saveAnswer(ConsultationPayloadDTO dto) {
//
//        User user = getCurrentUser();
//
//        Doctor doctor = doctorRepository
//                .findByUserId(user.getId())
//                .orElseThrow(() ->
//                        new DoctorException("Doctor not found"));
//
//        Patient patient =
//                patientRepository.getReferenceById(
//                        dto.getPatientId()
//                );
//
//        Appointment appointment =
//                appointmentRepository.getReferenceById(
//                        dto.getAppointmentId()
//                );
//
//        if (dto.getAnswers() == null ||
//                dto.getAnswers().isEmpty()) {
//
//            throw new RuntimeException(
//                    "Answers are empty"
//            );
//        }
//
//        Consultation consultation =
//                new Consultation();
//
//        StringBuilder notes =
//                new StringBuilder();
//
//        int completedAnswers = 0;
//
//        for (ConsultationAnswerDTO a : dto.getAnswers()) {
//
//            // ================= NOTES =================
//
//            notes.append("\nQ: ")
//                    .append(a.getQuestion());
//
//            notes.append("\nA: ")
//                    .append(a.getAnswer());
//
//            notes.append("\n");
//
//            // ================= COUNT VALID ANSWERS =================
//
//            if (a.getAnswer() != null &&
//                    !a.getAnswer().trim().isEmpty()) {
//
//                completedAnswers++;
//            }
//
//            // ================= AUTO MAP =================
//
//            mapStructuredFields(
//                    consultation,
//                    a
//            );
//        }
//
//        // =====================================================
//        // SMART STATUS MANAGEMENT
//        // =====================================================
//
//        if (completedAnswers == 0) {
//
//            consultation.setStatus(
//                    ConsultationStatus.CANCELLED
//            );
//
//        }
//
//        else if (
//                completedAnswers >= 7
//        ) {
//
//            consultation.setStatus(
//                    ConsultationStatus.COMPLETED
//            );
//
//        }
//
//        else {
//
//            consultation.setStatus(
//                    ConsultationStatus.IN_PROGRESS
//            );
//        }
//
//        // =====================================================
//
//        consultation.setNotes(
//                notes.toString()
//        );
//
//        consultation.setDoctor(doctor);
//
//        consultation.setPatient(patient);
//
//        consultation.setAppointment(
//                appointment
//        );
//
//        consultation.setCreatedAt(
//                LocalDateTime.now()
//        );
//
//        consultationServiceRepository
//                .save(consultation);
//    }

    @Override
    public ConsultationResponse getConsultationPagination(int pageNo, int pageSize, String sortBy, String sortDir, ConsultationFilter filter) {
        return iConsultationQuery.getConsultationPagination( pageNo, pageSize, sortBy, sortDir, filter);

    }
@Override
    public ConsultationDTO startConsultation(Long appointmentId) {
System.out.println("appointmentId"+appointmentId);
        User user = getCurrentUser();

        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new DoctorException("Doctor not found"));

    Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));

    System.out.println("Before: " + appointment.getStatus());
        appointment.setStatus(Status.IN_CONSULTATION);
        appointment=  appointmentRepository.save(appointment);


        Consultation consultation = new Consultation();
        consultation.setDoctor(doctor);
        consultation.setPatient(appointment.getPatient());
        consultation.setAppointment(appointment);
        consultation.setStatus(ConsultationStatus.IN_PROGRESS);
        consultation.setCreatedAt(LocalDateTime.now());
        consultation=consultationServiceRepository.save(consultation);
        return consultationMapper.mapToConsultationDTO(consultation);
    }

    public void saveAnswer(ConsultationPayloadDTO dto) {

        User user = getCurrentUser();

        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new DoctorException("Doctor not found"));

        Consultation consultation = consultationServiceRepository.findById(dto.getConsultationId())
                .orElseThrow(() -> new RuntimeException("Consultation not found"));

        if (!consultation.getDoctor().getId().equals(doctor.getId())) {
            throw new RuntimeException("Not your consultation");
        }

        if (dto.getAnswers() == null || dto.getAnswers().isEmpty()) {
            throw new RuntimeException("Answers are empty");
        }

        StringBuilder notes = new StringBuilder();

        for (ConsultationAnswerDTO a : dto.getAnswers()) {

            notes.append("\nQ: ").append(a.getQuestion());
            notes.append("\nA: ").append(a.getAnswer());

                // ==========================================
                // PRESCRIPTION PARSING
                // ==========================================

                if ("Prescription".equalsIgnoreCase(a.getQuestion())) {

                    String prescriptionText = a.getAnswer();

                    Prescription prescription = new Prescription();

                    String[] parts = prescriptionText.split("\\|");

                    for (String part : parts) {

                        String value = part.trim();

                        if (value.startsWith("Medicine:")) {

                            prescription.setMedicineName(
                                    value.replace("Medicine:", "").trim()
                            );
                        }

                        else if (value.startsWith("Dosage:")) {

                            prescription.setDosage(
                                    value.replace("Dosage:", "").trim()
                            );
                        }

                        else if (value.startsWith("Frequency:")) {

                            prescription.setFrequency(
                                    value.replace("Frequency:", "").trim()
                            );
                        }

                        else if (value.startsWith("Duration:")) {

                            prescription.setDuration(
                                    value.replace("Duration:", "").trim()
                            );
                        }

                        else if (value.startsWith("Instructions:")) {

                            prescription.setInstructions(
                                    value.replace("Instructions:", "").trim()
                            );
                        }
                    }

                    prescription.setConsultation(consultation);

                    prescriptionRepository.save(prescription);

                    continue;
                }


            mapStructuredFields(consultation, a);
        }

        consultation.setNotes(
                appendToNotes(consultation.getNotes(), notes.toString())
        );

        consultationServiceRepository.save(consultation);
    }
    public void endConsultation(Long consultationId) {
        Consultation consultation = consultationServiceRepository.findById(consultationId)
                .orElseThrow();

        Appointment appointment = consultation.getAppointment();

        consultation.setStatus(ConsultationStatus.COMPLETED);

        appointment.setStatus(Status.COMPLETED);
     //   appointment.setConsultationEndedAt(LocalDateTime.now());

        consultationServiceRepository.save(consultation);
        appointmentRepository.save(appointment);
    }






    private String normalize(String text) {
        if (text == null) return "";
        return text.toLowerCase().trim();
    }
    private void mapStructuredFields(
            Consultation consultation,
            ConsultationAnswerDTO a) {

        String question = normalize(a.getQuestion());
        String answer = a.getAnswer();

        System.out.println("q: " + question);
        System.out.println("answer: " + answer);

        // ================= SYMPTOMS =================
        if (match(question, "symptom")) {
            consultation.setSymptoms(answer);
            return;
        }

        // ================= START DATE =================
        if (match(question, "start")) {
            consultation.setSymptomsStartDate(answer);
            return;
        }

        // ================= PAIN =================
        if (match(question, "pain")) {
            consultation.setPain_level(answer);
            return;
        }

        // ================= ALLERGIES =================
        if (match(question, "allerg")) {
            consultation.setAllergies(answer);
            return;
        }

        // ================= MEDICATIONS =================
        if (match(question, "medication")) {
            consultation.setMedications(answer);
            return;
        }

        // ================= TEMPERATURE =================
        if (match(question, "temperature")) {
            consultation.setTemperature(parseDoubleSafe(answer));
            return;
        }

        // ================= HEART RATE =================
        if (match(question, "heart rate") || match(question, "heart")) {
            consultation.setHeartRate(parseIntSafe(answer));
            return;
        }

        // ================= BLOOD PRESSURE =================
        if (match(question, "blood pressure")) {
            consultation.setBloodPressure(answer);
            return;
        }
// ================= DIAGNOSIS =================

        if (match(question, "diagnosis")) {

            consultation.setDiagnosis(answer);

            return;
        }


        // ================= DEFAULT =================
        consultation.setNotes(
                appendToNotes(
                        consultation.getNotes(),
                        a.getQuestion() + ": " + answer
                )
        );
    }

    private boolean match(String question, String keyword) {
        return question != null && question.contains(keyword);
    }
    private Integer parseIntSafe(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return null;
        }
    }
    private Double parseDoubleSafe(String value) {
        try {
            return Double.parseDouble(value.trim());
        } catch (Exception e) {
            return null;
        }
    }


    private String appendToNotes(String existing, String newLine) {
        if (existing == null) return newLine;
        return existing + "\n" + newLine;
    }

    private Double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return null;
        }
    }

    private Integer parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return null;
        }
    }
}
