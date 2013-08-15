package app

import service.{AccountService, SystemSettingsService}
import SystemSettingsService._
import util.AdminAuthenticator
import jp.sf.amateras.scalatra.forms._
import org.scalatra.FlashMapSupport

class SystemSettingsController extends SystemSettingsControllerBase
  with SystemSettingsService with AccountService with AdminAuthenticator

trait SystemSettingsControllerBase extends ControllerBase with FlashMapSupport {
  self: SystemSettingsService with AccountService with AdminAuthenticator =>

  private val form = mapping(
    "allowAccountRegistration" -> trim(label("Account registration", boolean())),
    "gravatar"                 -> trim(label("Gravatar", boolean())),
    "notification"             -> trim(label("Notification", boolean())),
    "smtp"                     -> optionalIfNotChecked("notification", mapping(
        "host"                     -> trim(label("SMTP Host", text(required))),
        "port"                     -> trim(label("SMTP Port", optional(number()))),
        "user"                     -> trim(label("SMTP User", optional(text()))),
        "password"                 -> trim(label("SMTP Password", optional(text()))),
        "ssl"                      -> trim(label("Enable SSL", optional(boolean())))
    )(Smtp.apply)),
    "authType"                -> trim(label("Auth Type", text(required))),
    "ldap"                    -> optional(_.get("authType") == Some("LDAP"), mapping(
        "host"                    -> trim(label("LDAP host", text(required))),
        "port"                    -> trim(label("LDAP port", number(required))),
        "baseDN"                  -> trim(label("BaseDN", text(required))),
        "userNameAttribute"       -> trim(label("User name attribute", text(required))),
        "mailAttribute"           -> trim(label("Mail address attribute", text(required)))
    )(Ldap.apply))
  )(SystemSettings.apply)


  get("/admin/system")(adminOnly {
    admin.html.system(loadSystemSettings(), flash.get("info"))
  })

  post("/admin/system", form)(adminOnly { form =>
    saveSystemSettings(form)
    flash += "info" -> "System settings has been updated."
    redirect("/admin/system")
  })

}
