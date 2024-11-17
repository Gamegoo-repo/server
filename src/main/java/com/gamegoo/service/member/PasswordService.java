package com.gamegoo.service.member;

import com.gamegoo.apiPayload.code.status.ErrorStatus;
import com.gamegoo.apiPayload.exception.handler.MemberHandler;
import com.gamegoo.domain.member.Member;
import com.gamegoo.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JavaMailSender javaMailSender;


    /**
     * 비밀번호가 맞는지 확인
     *
     * @param userId
     * @param password
     * @return
     */
    public boolean checkPasswordById(Long userId, String password) {
        return memberRepository.findById(userId)
                .map(member -> bCryptPasswordEncoder.matches(password, member.getPassword()))
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

    /**
     * 비밀번호 수정 (로그인O)
     *
     * @param userId
     * @param newPassword
     */
    public void updatePasswordById(Long userId, String oldPassword, String newPassword) {
        // jwt 토큰으로 멤버 찾기
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        boolean matches = bCryptPasswordEncoder.matches(oldPassword, member.getPassword());

        if (matches) {
            // 비밀번호 재설정
            member.updatePassword(bCryptPasswordEncoder.encode(newPassword));
            memberRepository.save(member);
        }else{
            throw new MemberHandler(ErrorStatus.PASSWORD_INVALID);
        }

    }

    /**
     * 비밀번호 수정 (로그인X)
     *
     * @param email
     */
    public void updatePasswordWithEmail(String email, String newPassword) {
        // email으로 멤버 찾기
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // 비밀번호 재설정
        member.updatePassword(bCryptPasswordEncoder.encode(newPassword));
        memberRepository.save(member);

    }

    /**
     * Gmail 발송
     *
     * @param email
     * @param tempPassword
     */
    public void sendEmailInternal(String email, String tempPassword) {
        try {
            log.info("Starting email send process for email: {}, certificationNumber: {}", email,
                    tempPassword);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);

            String htmlContent = getTempPasswordMessage(tempPassword);

            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("GameGoo 임시 비밀번호 발급");
            mimeMessageHelper.setText(htmlContent, true);
            log.debug("Prepared email message for email: {}", email);

            javaMailSender.send(message);
            log.info("Email sent successfully to email: {}", email);


        } catch (MessagingException e) {
            log.error("Failed to send email to email: {}, certificationNumber: {}, error: {}",
                    email, tempPassword, e.getMessage());

            throw new MemberHandler(ErrorStatus.EMAIL_SEND_ERROR);
        }
    }

    /**
     * 메일 내용 편집
     *
     * @param tempPassword
     * @return
     */
    private String getTempPasswordMessage(String tempPassword) {
        String certificationMessage = ""
                + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"
                +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                "  <head>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
                "    <title>Gamegoo 임시 비밀번호 발급</title>\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <table\n" +
                "      style=\"\n" +
                "        width: 628px;\n" +
                "        box-sizing: border-box;\n" +
                "        border-collapse: collapse;\n" +
                "        background-color: #ffffff;\n" +
                "        border: 1px solid #c0c0c0;\n" +
                "        text-align: left;\n" +
                "        margin: 0 auto;\n" +
                "      \"\n" +
                "    >\n" +
                "      <tbody>\n" +
                "        <tr>\n" +
                "          <td>\n" +
                "            <table\n" +
                "              cellpadding=\"0\"\n" +
                "              cellspacing=\"0\"\n" +
                "              style=\"width: 628px; height: 521px; padding: 53px 62px 42px 62px\"\n" +
                "            >\n" +
                "              <tbody>\n" +
                "                <tr>\n" +
                "                  <td style=\"padding-bottom: 11.61px\">\n" +
                "                    <img\n" +
                "                      src=\"https://ifh.cc/g/BY3XG2.png\"\n" +
                "                      style=\"display: block\"\n" +
                "                      width=\"137\"\n" +
                "                      height=\"24\"\n" +
                "                      alt=\"Gamegoo\"\n" +
                "                    />\n" +
                "                  </td>\n" +
                "                </tr>\n" +
                "                <tr>\n" +
                "                  <td style=\"padding-top: 20px\">\n" +
                "                    <span\n" +
                "                      style=\"\n" +
                "                        color: #2d2d2d;\n" +
                "                        font-family: Pretendard;\n" +
                "                        font-size: 25px;\n" +
                "                        font-style: normal;\n" +
                "                        font-weight: 400;\n" +
                "                        line-height: 150%;\n" +
                "                      \"\n" +
                "                    >\n" +
                "                      임시 비밀번호를 확인해주세요\n" +
                "                    </span>\n" +
                "                  </td>\n" +
                "                </tr>\n" +
                "                <tr>\n" +
                "                  <td style=\"padding-top: 38px\">\n" +
                "                    <span\n" +
                "                      style=\"\n" +
                "                        color: #5a42ee;\n" +
                "                        color: #2d2d2d;\n" +
                "                        font-size: 32px;\n" +
                "                        font-style: normal;\n" +
                "                        font-weight: 700;\n" +
                "                        line-height: 150%;\n" +
                "                        margin-bottom: 30px;\n" +
                "                      \"\n" +
                "                    >\n" +
                tempPassword +
                "                    </span>\n" +
                "                  </td>\n" +
                "                </tr>\n" +
                "                <tr>\n" +
                "                  <td style=\"padding-top: 30px\">\n" +
                "                    <span\n" +
                "                      style=\"\n" +
                "                        color: #2d2d2d;\n" +
                "                        font-family: Pretendard;\n" +
                "                        font-size: 18px;\n" +
                "                        font-style: normal;\n" +
                "                        font-weight: 400;\n" +
                "                        line-height: 150%;\n" +
                "                      \"\n" +
                "                    >\n" +
                "                      임시 비밀번호가 발급되었습니다. \n" +
                "                      감사합니다.\n" +
                "                    </span>\n" +
                "                  </td>\n" +
                "                </tr>\n" +
                "              </tbody>\n" +
                "            </table>\n" +
                "            <table\n" +
                "              cellpadding=\"0\"\n" +
                "              cellspacing=\"0\"\n" +
                "              style=\"\n" +
                "                width: 628px;\n" +
                "                height: 292px;\n" +
                "                padding: 37px 0px 153px 62px;\n" +
                "                background: #f7f7f9;\n" +
                "              \"\n" +
                "            >\n" +
                "              <tbody>\n" +
                "                <tr>\n" +
                "                  <td>\n" +
                "                    <span\n" +
                "                      style=\"\n" +
                "                        color: #606060;\n" +
                "                        font-family: Pretendard;\n" +
                "                        font-size: 11px;\n" +
                "                        font-style: normal;\n" +
                "                        font-weight: 500;\n" +
                "                        line-height: 150%;\n" +
                "                      \"\n" +
                "                    >\n" +
                "                      본 메일은 발신 전용으로 회신되지 않습니다.<br />\n" +
                "                      궁금하신 점은 겜구 이메일이나 카카오 채널을 통해\n" +
                "                      문의하시기 바랍니다.<br /><br />\n" +
                "                      email: gamegoo0707@gmail.com<br />\n" +
                "                      kakao: https://pf.kakao.com/_Rrxiqn<br />\n" +
                "                      copyright 2024. GameGoo All Rights Reserved.<br />\n" +
                "                    </span>\n" +
                "                  </td>\n" +
                "                </tr>\n" +
                "              </tbody>\n" +
                "            </table>\n" +
                "          </td>\n" +
                "        </tr>\n" +
                "      </tbody>\n" +
                "    </table>\n" +
                "  </body>\n" +
                "</html>\n";

        return certificationMessage;
    }

}
