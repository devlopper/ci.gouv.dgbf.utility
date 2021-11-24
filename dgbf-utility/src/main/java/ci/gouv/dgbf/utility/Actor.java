package ci.gouv.dgbf.utility;

import java.io.Serializable;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.constant.ConstantEmpty;
import org.cyk.utility.__kernel__.field.FieldHelper;
import org.cyk.utility.__kernel__.log.LogHelper;
import org.cyk.utility.__kernel__.protocol.http.HttpClientGetter;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.throwable.ThrowableHelper;
import org.cyk.utility.__kernel__.value.ValueHelper;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Actor implements Serializable {
	
	@SerializedName("username") private String username;
	@SerializedName("firstName") private String firstName;
	@SerializedName("lastNames") private String lastNames;
	@SerializedName("names") private String names;
	@SerializedName("sectionAsString") private String section;
	@SerializedName("administrativeUnitAsString") private String administrativeUnit;
	@SerializedName("administrativeFunction") private String administrativeFunction;
	@SerializedName("electronicMailAddress") private String electronicMailAddress;
	private Visibilities visibleSections;
	private Visibilities visibleBudgetSpecialisationUnits;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
	
	public static final String GET_INFORMATIONS_URL_PATH = "api/actor/informations-profile-par-acteur";
	public static final String GET_INFORMATIONS_URL_FORMAT = "http://%s:%s/%s?nom_utilisateur=%s&json_server_result_singular=1";
	public static Actor getInformations(String username) {
		ThrowableHelper.throwIllegalArgumentExceptionIfBlank("username", username);
		URI uri = URI.create(String.format(GET_INFORMATIONS_URL_FORMAT, HOST,PORT,GET_INFORMATIONS_URL_PATH,username));
		LogHelper.logInfo("Actor.getInformations.uri : "+uri.toString(), Actor.class);
		String json = null;
		try {
			HttpResponse<String> response = HttpClientGetter.getInstance().get().send(
					HttpRequest.newBuilder().uri(uri).build(), BodyHandlers.ofString());
			json = response.body();
		} catch (Exception exception) {
			LogHelper.log(exception, Actor.class);
		}
		if(StringHelper.isBlank(json)) {
			LogHelper.logSevere("cannot get actor informations with username "+username+" from URI "+uri, Actor.class);
			return null;
		}
		Actor actor = new Gson().fromJson(json, Actor.class);
		if(actor == null) {
			LogHelper.logSevere("cannot build informations of actor with username "+username+" from json "+json, Actor.class);
			return null;
		}
		if(StringHelper.isBlank(actor.getUsername()))
			actor.setUsername(username);
		if(StringHelper.isBlank(actor.getNames()))
			actor.setNames(StringUtils.trim(actor.getFirstName()+" "+ValueHelper.defaultToIfBlank(actor.getLastNames(), ConstantEmpty.STRING)));
		LogHelper.logInfo("Informations of actor with username "+username+" : "+actor, Actor.class);
		return actor;
	}
	
	public static final String GET_VISIBLE_SECTIONS_CODES_URL_PATH = "api/domaine/sections_par_acteur";
	public static final String GET_VISIBLE_SECTIONS_CODES_URL_FORMAT = "http://%s:%s/%s?nom_utilisateur=%s";
	public static Collection<Visibility> getVisibleSections(String username) {
		ThrowableHelper.throwIllegalArgumentExceptionIfBlank("username", username);
		URI uri = URI.create(String.format(GET_VISIBLE_SECTIONS_CODES_URL_FORMAT, HOST,PORT,GET_VISIBLE_SECTIONS_CODES_URL_PATH,username));
		LogHelper.logInfo("Actor.getVisibleSections.uri : "+uri.toString(), Actor.class);
		String json = null;
		try {
			HttpResponse<String> response = HttpClientGetter.getInstance().get().send(
					HttpRequest.newBuilder().uri(uri).build(), BodyHandlers.ofString());
			json = response.body();
		} catch (Exception exception) {
			LogHelper.log(exception, Actor.class);
		}
		if(StringHelper.isBlank(json)) {
			LogHelper.logSevere("cannot get actor visible sections with username "+username+" from URI "+uri, Actor.class);
			return null;
		}
		Collection<Visibility> visibleSections = new Gson().fromJson(json, new TypeToken<List<Visibility>>(){}.getType());
		if(CollectionHelper.isEmpty(visibleSections)) {
			LogHelper.logInfo("visible sections of actor with username "+username+" are empty ",Actor.class);
			return null;
		}
		LogHelper.logInfo("visible sections of actor with username "+username+" : "+visibleSections,Actor.class);
		return visibleSections;
	}
	
	public static Actor getInformationsAndVisibilities(String username) {
		ThrowableHelper.throwIllegalArgumentExceptionIfBlank("username", username);
		Actor actor = getInformations(username);
		actor.setVisibleSections(new Visibilities(getVisibleSections(username)));
		//actor.setVisibleBudgetSpecialisationUnits(new Visibility<VisibleBudgetSpecialisationUnit>(getVisible));
		return actor;
	}
	
	/**/

	/**/
	
	public static final String HOST = Helper.getApiHost("acteur");
	public static final Short PORT = Helper.getApiPort("acteur");
	
	/**/
	
	public static class Visibility {
		@SerializedName("identifiant") public String identifier;
		@SerializedName("code") public String code;
		@SerializedName("name") public String name;
		
		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
		}
	}
	
	public static class Visibilities {
		public Collection<Visibility> collection;
		public Collection<String> identifiers;
		public Collection<String> codes;
		
		Visibilities(Collection<Visibility> collection) {
			this.collection = collection;			
			if(CollectionHelper.isEmpty(this.collection)) {
				this.identifiers = new ArrayList<>();
				this.codes = new ArrayList<>();
			}else {
				this.identifiers = this.collection.stream().map(x -> StringHelper.get(FieldHelper.readSystemIdentifier(x))).collect(Collectors.toList());
				this.codes = this.collection.stream().map(x -> StringHelper.get(FieldHelper.readBusinessIdentifier(x))).collect(Collectors.toList());
			}
		}
		
		@Override
		public String toString() {
			return collection == null ? ConstantEmpty.STRING : collection.toString();
		}
	}
}