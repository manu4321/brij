package ca.brij.validation;

import ca.brij.bean.user.User;
import ca.brij.utils.ConstantsUtil;

public class Validator {

	public static boolean lengthValid(int min, int max, String value) {

		return !(value.length() < min || value.length() > max);
	}

	public static boolean valueValid(String value) {

		return !(value == null);
	}

	public static String userRegisterValid(User userEntity) {

		String exceptions = "";
		String userName = userEntity.getUsername();

		if (!Validator.valueValid(userName)) {
			exceptions = ConstantsUtil.NULL_USERNAME + ";";
		} else {
			if (!Validator.lengthValid(5, 20, userName)) {
				exceptions = ConstantsUtil.USERNAME_LENGTH + ";";
			}
		}

		String password = userEntity.getPassword();

		if (!Validator.valueValid(password)) {
			exceptions += ConstantsUtil.NULL_PASSWORD + ";";
		} else {
			if (password.length() < 5 || password.length() > 15) {
				exceptions += ConstantsUtil.PASSWORD_LENGTH + ";";
			}
		}

		String email = userEntity.getEmail();

		if (!Validator.valueValid(email)) {
			exceptions += ConstantsUtil.NULL_EMAIL + ";";
		} else {
			if (email.length() < 6 || email.length() > 30) {
				exceptions += ConstantsUtil.EMAIL_ADDRESS + ";";
			}
		}


		return exceptions;
	}
	
	public static String userEntityValid(User userEntity) {

		String exceptions = "";
		String userName = userEntity.getUsername();

		if (!Validator.valueValid(userName)) {
			exceptions = ConstantsUtil.NULL_USERNAME + ";";
		} else {
			if (!Validator.lengthValid(5, 20, userName)) {
				exceptions = ConstantsUtil.USERNAME_LENGTH + ";";
			}
		}

		String password = userEntity.getPassword();

		if (!Validator.valueValid(password)) {
			exceptions += ConstantsUtil.NULL_PASSWORD + ";";
		} else {
			if (password.length() < 5 || password.length() > 15) {
				exceptions += ConstantsUtil.PASSWORD_LENGTH + ";";
			}
		}

		String email = userEntity.getEmail();

		if (!Validator.valueValid(email)) {
			exceptions = ConstantsUtil.NULL_EMAIL + ";";
		} else {
			if (email.length() < 6 || email.length() > 30) {
				exceptions = ConstantsUtil.EMAIL_ADDRESS + ";";
			}
		}

		String firstName = userEntity.getFirstName();

		if (!Validator.valueValid(firstName)) {
			exceptions = ConstantsUtil.NULL_FIRST_NAME + ";";
		} else {
			if (firstName.length() < 2 || firstName.length() > 25) {
				exceptions = ConstantsUtil.FIRST_NAME + ";";
			}
		}

		String lastName = userEntity.getLastName();

		if (!Validator.valueValid(lastName)) {
			exceptions = ConstantsUtil.NULL_LAST_NAME + ";";
		} else {
			if (lastName.length() < 2 || lastName.length() > 25) {
				exceptions = ConstantsUtil.LAST_NAME + ";";
			}
		}

		String phoneNumber = userEntity.getPhoneNumber();

		if (!Validator.valueValid(phoneNumber)) {
			exceptions = ConstantsUtil.NULL_PHONE_NUMBER + ";";
		} else {
			if (phoneNumber.length() < 10 || phoneNumber.length() > 12) {
				exceptions = ConstantsUtil.PHONE_NUMBER + ";";
			}
		}

		String address = userEntity.getAddress();

		if (!Validator.valueValid(address)) {
			exceptions = ConstantsUtil.NULL_ADDRESS + ";";
		}

		String city = userEntity.getCity();

		if (!Validator.valueValid(city)) {
			exceptions = ConstantsUtil.NULL_CITY + ";";
		} else {
			if (city.length() < 3 || city.length() > 35) {
				exceptions = ConstantsUtil.CITY + ";";
			}
		}

		String province = userEntity.getProvince();

		if (!Validator.valueValid(province)) {
			exceptions = ConstantsUtil.NULL_PROVINCE + ";";
		} else {
			if (province.length() < 2 || province.length() > 35) {
				exceptions = ConstantsUtil.PROVINCE + ";";
			}
		}

		return exceptions;
	}
}
